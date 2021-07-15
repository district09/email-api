package gent.d09.servicefactory.email.api.module.email.service;

import gent.d09.servicefactory.email.api.container.ApplicationConfig;
import gent.d09.servicefactory.email.api.module.common.service.Tracer;
import gent.d09.servicefactory.email.api.module.common.util.QueryVisitor;
import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailCreationDto;
import gent.d09.servicefactory.email.api.module.email.domain.event.EmailStatusEvent;
import gent.d09.servicefactory.email.api.module.email.util.EmailUtil;
import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailCreationResponseDto;
import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailDto;
import gent.d09.servicefactory.email.api.module.email.domain.entity.EmailEntity;
import gent.d09.servicefactory.email.api.module.common.domain.dto.CollectionFilter;
import gent.d09.servicefactory.email.api.module.email.domain.event.EmailCreationEvent;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import lombok.SneakyThrows;
import org.apache.http.HttpStatus;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataMessageException;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class EmailService  {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Producer producer;
    private final ApplicationConfig applicationConfig;
    private Map<String, CompletableFuture<EmailCreationResponseDto>> pendingFutures;

    @ConfigProperty(name = "quarkus.resteasy.path")
    private String namespace;

    public EmailService(Producer producer, ApplicationConfig applicationConfig) {
        this.producer = producer;
        this.applicationConfig = applicationConfig;
        this.pendingFutures = new HashMap<>();
    }

    @SneakyThrows
    @Transactional
    public Future<EmailCreationResponseDto> createEmail(EmailCreationDto dto, SecurityContext context) {
        EmailUtil.validateDto(dto);

        List<String> allowedAddresses = applicationConfig.getSecurityMap().get(context.getUserPrincipal().getName());
        if(allowedAddresses == null || !allowedAddresses.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(EmailUtil.getRawEmailAddress(dto.getFrom()).toLowerCase())){
            throw new WebApplicationException("This application is not allowed to send messages from the e-mail address " + dto.getFrom(), HttpStatus.SC_FORBIDDEN);
        }

        EmailEntity entity = new EmailEntity(dto, context.getUserPrincipal().getName());
        EmailEntity.persist(entity);

        CompletableFuture<EmailCreationResponseDto> future = new CompletableFuture<>();
        if (entity.async) {
            // Return entity now
            future.complete(EmailCreationResponseDto.builder()
                    .locations(Collections.singletonList(new URI("/" + namespace + "/v3/emails/" + entity.id))) // TODO: fix uri using namespace config
                    .build()
            );
        } else {
            // Block until document is generated
            pendingFutures.put(Tracer.getCorrelationId(), future);
        }

        this.producer.send(EmailCreationEvent.builder()
            .correlationId(Tracer.getCorrelationId())
            .id(entity.id)
            .applicationId(entity.applicationId)
            .from(dto.from)
            .to(EmailUtil.splitCommaSeparatedEmails(dto.to))
            .cc(EmailUtil.splitCommaSeparatedEmails(dto.cc))
            .bcc(EmailUtil.splitCommaSeparatedEmails(dto.bcc))
            .replyTo(dto.replyTo)
            .subject(dto.subject)
            .text(dto.text)
            .html(dto.html)
            .attachments(dto.attachments)
            .inlineImages(dto.inlineImages)
            .build()
        );

        return future;
    }

    public EmailDto getEmail(Long id, SecurityContext context) {
        EmailEntity emailEntity = (EmailEntity) EmailEntity.findByIdOptional(id).orElseThrow(() ->
            new WebApplicationException("Could not find entity with id " + id, HttpStatus.SC_NOT_FOUND)
        );
        if(!context.getUserPrincipal().getName().equals(emailEntity.getApplicationId())) {
            throw new WebApplicationException("Forbidden. This resource can only be accessed by its owner", HttpStatus.SC_FORBIDDEN);
        }
        return EmailDto.builder()
                .id(emailEntity.id)
                .status(emailEntity.status)
                .statusMessage(emailEntity.statusMessage)
                .build();
    }

    public List<EmailDto> listEmails(CollectionFilter filter, SecurityContext context) {
        String filterQuery = "applicationId = '" + context.getUserPrincipal().getName() + "'";
        PanacheQuery<EmailEntity> panacheQuery = EmailEntity.find(filterQuery);

        QueryVisitor queryVisitor = new QueryVisitor(Set.of("status", "statusMessage", "async"));
        if(filter.getFilter() != null && !filter.getFilter().isEmpty()) {
            try {
                FilterExpression filterExpression = UriParser.parseFilter(null, null, filter.getFilter());
                filterQuery = filterQuery + " and (" + filterExpression.accept(queryVisitor) + ")";
                panacheQuery = EmailEntity.find(filterQuery);
            } catch (ODataMessageException | ODataApplicationException e) {
                throw new WebApplicationException("Invalid filter query: " + filter.getFilter() + " - " + e.getMessage(), HttpStatus.SC_BAD_REQUEST);
            }
        }

        if(filter.getOrderBy() != null && !filter.getOrderBy().isEmpty()){
            try {
                OrderByExpression orderByExpression = UriParser.parseOrderBy(null, null, filter.getOrderBy()); // "id asc, status desc"
                Sort sort = (Sort) orderByExpression.accept(queryVisitor);
                panacheQuery = EmailEntity.find(filterQuery, sort);
            } catch (ODataMessageException | ODataApplicationException e) {
                throw new WebApplicationException("Invalid orderBy query: " + filter.getOrderBy() + " - " + e.getMessage(), HttpStatus.SC_BAD_REQUEST);
            }
        }

        int offset = Optional.ofNullable(filter.getOffset()).orElse(0);
        int limit = Optional.ofNullable(filter.getLimit()).orElse(20);
        panacheQuery = panacheQuery.range(offset, Math.max(offset + limit - 1, 0));

        return panacheQuery.list().stream().map(entity ->
            EmailDto.builder()
                .id(entity.id)
                .statusMessage(entity.statusMessage)
                .status(entity.status)
                .build()).collect(Collectors.toList());
    }

    @SneakyThrows
    @Transactional
    public void handleStatusEvent(EmailStatusEvent event) {
        String correlationId = event.getCorrelationId();
        Tracer.setCorrelationId(correlationId);

        // Get entity
        EmailEntity entity = (EmailEntity) EmailEntity.findByIdOptional(event.getId()).orElseThrow(() ->
            new WebApplicationException("Could not find Email with id " + event.getId())
        );

        // Update status (avoid overwriting "Sent" and "Failed" with "InProgress" status when messages arrive out of order)
        if(!"InProgress".equalsIgnoreCase(event.getStatus())
                || (!"Sent".equalsIgnoreCase(entity.getStatus()) && (!"Failed".equalsIgnoreCase(entity.getStatus())))) {
            entity.setStatus(event.getStatus());
            entity.setStatusMessage(event.getStatusMessage());
            EmailEntity.persist(entity);
        }

        if(!"InProgress".equalsIgnoreCase(entity.getStatus()) && pendingFutures.containsKey(correlationId)) {
            // Complete and remove the CompletableFuture
            CompletableFuture<EmailCreationResponseDto> future = pendingFutures.get(correlationId);
            pendingFutures.remove(correlationId);
            future.complete(EmailCreationResponseDto.builder()
                .locations(Collections.singletonList(new URI("/" + namespace + "/v3/emails/" + entity.id)))
                .build()
            );
        }
    }

    @Transactional
    public void archive() {
        EmailEntity.delete("created_at < :date", Parameters.with("date", new Date()));
    }
}