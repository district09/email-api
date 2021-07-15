package gent.d09.servicefactory.email.api.module.email.rest;

import gent.d09.servicefactory.email.api.module.email.service.EmailService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v3/historiek")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoryController {
    private final EmailService emailService;

    public HistoryController(EmailService emailService) {
        this.emailService = emailService;
    }

    @DELETE
    public Response archive() {
        emailService.archive();
        return Response.accepted().build();
    }
}
