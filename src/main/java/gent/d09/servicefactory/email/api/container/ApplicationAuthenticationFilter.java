package gent.d09.servicefactory.email.api.container;

import org.apache.http.HttpStatus;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;
import java.util.regex.Pattern;

// https://quarkus.io/guides/security-customization#custom-jax-rs-securitycontext

@Provider
@PreMatching
public class ApplicationAuthenticationFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if(!Pattern.compile("\\/v3\\/status\\/.*").matcher(requestContext.getUriInfo().getPath()).matches()) {
            String applicationId = requestContext.getHeaders().getFirst("applicationId");
            if (applicationId == null) {
                throw new WebApplicationException("Authorization parameter to access this resource missing", HttpStatus.SC_FORBIDDEN);
            }
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> applicationId;
                }

                @Override
                public boolean isUserInRole(String s) {
                    return true;
                }

                @Override
                public boolean isSecure() {
                    return true;
                }

                @Override
                public String getAuthenticationScheme() {
                    return "basic";
                }
            });
        }
    }
}
