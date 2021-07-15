package gent.d09.servicefactory.email.api.module.common.service;

import gent.d09.servicefactory.email.api.module.common.domain.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /*@Context
    private HttpServletRequest request;*/

    @Override
    public Response toResponse(Exception e) {
        log.error("Request failed: " + e.getMessage());

        int code = 500;
        if(e instanceof WebApplicationException) {
            code = ((WebApplicationException) e).getResponse().getStatus();
        }
        return Response.status(code).entity(ApiError.of(e, code + "", null)).build();
    }
}
