package gent.d09.servicefactory.email.api.module.email.rest;

import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailCreationDto;
import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailCreationResponseDto;
import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailDto;
import gent.d09.servicefactory.email.api.module.common.domain.dto.CollectionFilter;
import gent.d09.servicefactory.email.api.module.email.service.EmailService;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/v3/emails")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmailController {
    private final EmailService emailService;
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GET
    public List<EmailDto> listAllEmails(@BeanParam CollectionFilter filter, @Context SecurityContext securityContext) {
        return emailService.listEmails(filter, securityContext);
    }

    @POST
    public Response createEmail(EmailCreationDto dto, @Context SecurityContext securityContext) throws ExecutionException, InterruptedException {
        EmailCreationResponseDto response = this.emailService.createEmail(dto, securityContext).get();
        return Response.created(response.getLocations().get((0))).entity(response).build();
    }

    @GET
    @Path("{id}")
    public EmailDto getEmail(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        return this.emailService.getEmail(id, securityContext);
    }
}