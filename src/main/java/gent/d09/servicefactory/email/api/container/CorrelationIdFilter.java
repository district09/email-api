package gent.d09.servicefactory.email.api.container;

import gent.d09.servicefactory.email.api.module.common.service.Tracer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class CorrelationIdFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Tracer.setCorrelationId(requestContext.getHeaders().getFirst("correlationId"));
    }
}
