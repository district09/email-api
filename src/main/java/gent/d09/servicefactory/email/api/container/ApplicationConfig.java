package gent.d09.servicefactory.email.api.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;

import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Map;

@ConfigProperties(prefix = "application")
@Data
@NoArgsConstructor
public class ApplicationConfig {
    private String security = null;

    public Map<String, List<String>> getSecurityMap() {
        if(security == null) {
            throw new WebApplicationException("Environment variable APPLICATION_SECURITY is missing", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        try {
            return new ObjectMapper().readValue(security, Map.class);
        } catch (JsonProcessingException e) {
            throw new WebApplicationException("Environment variable APPLICATION_SECURITY is invalid", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
