package gent.d09.servicefactory.email.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailCreationDto;
import gent.d09.servicefactory.email.api.module.email.domain.entity.EmailEntity;
import gent.d09.servicefactory.email.api.module.email.domain.event.EmailCreationEvent;
import gent.d09.servicefactory.email.api.module.email.service.Producer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@QuarkusTest
@Transactional
@QuarkusTestResource(H2DatabaseTestResource.class)
public class EmailControllerTest {
    private static final String APPLICATION_ID_HEADER = "applicationId";
    private static final String APPLICATION_ID_0 = "my_application_id";
    private static final String APPLICATION_ID_1 = "my_other_application_id";
    private List<EmailEntity> emailEntityList;

    @ConfigProperty(name = "quarkus.resteasy.path")
    private String resteasyPath;

    @BeforeEach
    public void setup() {
        // Seed database
        this.seedDatabase();
    }

    @AfterEach
    public void clean() {
        // Clear database
        this.clearDatabase();
    }

    @BeforeAll
    public static void mock() {
        Producer producer = Mockito.mock(Producer.class);
        Mockito.doNothing().when(producer).send(any(EmailCreationEvent.class));
        QuarkusMock.installMockForType(producer, Producer.class);
    }

    @Test
    public void shouldReturnEmailEntityList() throws Exception {
        String body = given()
            .accept(ContentType.JSON)
            .request()
            .contentType(ContentType.JSON)
            .when()
            .header(APPLICATION_ID_HEADER, APPLICATION_ID_0)
            .get("/" + resteasyPath + "/v3/emails")
            .then().statusCode(200)
            .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("[{\"id\":" + emailEntityList.get(0).id + ",\"status\":\"Queued\",\"statusMessage\":\"Queued\"}, {\"id\":" + emailEntityList.get(1).id + ",\"status\":\"Queued\",\"statusMessage\":\"Queued\"}]");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldReturnEmailEntity() throws Exception {
        String body = given()
                .accept(ContentType.JSON)
                .request()
                .contentType(ContentType.JSON)
                .when()
                .header(APPLICATION_ID_HEADER, APPLICATION_ID_0)
                .get("/" + resteasyPath + "/v3/emails/" + emailEntityList.get(0).id)
                .then().statusCode(200)
                .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{\"id\":" + emailEntityList.get(0).id + ",\"status\":\"Queued\",\"statusMessage\":\"Queued\"}");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldNotReturnEmailEntity() throws Exception {
        String body = given()
                .accept(ContentType.JSON)
                .request()
                .contentType(ContentType.JSON)
                .when()
                .header(APPLICATION_ID_HEADER, APPLICATION_ID_1)
                .get("/" + resteasyPath + "/v3/emails/" + emailEntityList.get(0).id)
                .then().statusCode(403)
                .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{ \"code\": \"403\", \"message\": \"Forbidden. This resource can only be accessed by its owner\" } ");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldNotReturnEmailCollection() throws Exception {
        String body = given()
                .accept(ContentType.JSON)
                .request()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + resteasyPath + "/v3/emails")
                .then().statusCode(403)
                .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{ \"code\": \"403\", \"message\": \"Authorization parameter to access this resource missing\" } ");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldNotDeleteHistory() throws Exception {
        String body = given()
                .accept(ContentType.JSON)
                .request()
                .contentType(ContentType.JSON)
                .when()
                .delete("/" + resteasyPath + "/v3/historiek")
                .then().statusCode(403)
                .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{ \"code\": \"403\", \"message\": \"Authorization parameter to access this resource missing\" }");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldReturnForbidden() throws Exception {
        String body = given()
                .accept(ContentType.JSON)
                .request()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + resteasyPath + "/v3/emails/" + emailEntityList.get(0).getId())
                .then().statusCode(403)
                .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{ \"code\": \"403\", \"message\": \"Authorization parameter to access this resource missing\" }");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldReturnNotFound() throws Exception {
        long unknownId = (long) (Math.random() * 100000.0);
        String body = given()
                .accept(ContentType.JSON)
                .request()
                .contentType(ContentType.JSON)
                .when()
                .header(APPLICATION_ID_HEADER, APPLICATION_ID_0)
                .get("/" + resteasyPath + "/v3/emails/" + unknownId)
                .then().statusCode(404)
                .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{ \"code\": \"404\", \"message\": \"Could not find entity with id " + unknownId + "\"} ");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldReturnAcceptedOnDelete() throws Exception {
        given()
            .accept(ContentType.JSON)
            .request()
            .contentType(ContentType.JSON)
            .when()
            .header(APPLICATION_ID_HEADER, APPLICATION_ID_0)
            .delete("/" + resteasyPath + "/v3/historiek")
            .then().statusCode(202)
            .extract();
    }

    @Test
    public void shouldCreateEmailEntity() throws Exception {
        given()
            .accept(ContentType.JSON)
            .request()
            .contentType(ContentType.JSON)
            .with().body("{ \"from\": \"test@mail.be\", \"to\": \"to2@test.com\", \"cc\": \"cc2@test.com, cc3 <cc3@test.com>\", \"html\": \"<html><body><p>My HTML message</p></body></html>\", \"replyTo\": \"replyTo2@test.com\", \"subject\": \"my_subject2\" }")
            .when()
            .header(APPLICATION_ID_HEADER, APPLICATION_ID_0)
            .post("/" + resteasyPath + "/v3/emails")
            .then().statusCode(201);
    }

    @Test
    public void shouldNotAllowToCreateEmailEntity() throws Exception {
        String body = given()
            .accept(ContentType.JSON)
            .request()
            .contentType(ContentType.JSON)
            .with().body("{ \"from\": \"from2@test.com\", \"to\": \"to2@test.com\", \"cc\": \"cc2@test.com, cc3 <cc3@test.com>\", \"html\": \"<html><body><p>My HTML message</p></body></html>\", \"replyTo\": \"replyTo2@test.com\", \"subject\": \"my_subject2\", \"tags\": [ { \"name\": \"my_tag2\", \"value\": \"my_value2\" } ] } ")
            .when()
            .header(APPLICATION_ID_HEADER, APPLICATION_ID_1)
            .post("/" + resteasyPath + "/v3/emails")
            .then().statusCode(403)
            .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{ \"code\": \"403\", \"message\": \"This application is not allowed to send messages from the e-mail address from2@test.com\" } ");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldNotCreateEmailEntityAndReturnForbidden() throws Exception {
        String body = given()
                .accept(ContentType.JSON)
                .request()
                .contentType(ContentType.JSON)
                .with().body("{ \"from\": \"from2@test.com\", \"to\": \"to2@test.com\", \"cc\": \"cc2@test.com, cc3 <cc3@test.com>\", \"html\": \"<html><body><p>My HTML message</p></body></html>\", \"replyTo\": \"replyTo2@test.com\", \"subject\": \"my_subject2\", \"tags\": [ { \"name\": \"my_tag2\", \"value\": \"my_value2\" } ] } ")
                .when()
                .header(APPLICATION_ID_HEADER, APPLICATION_ID_1)
                .post("/" + resteasyPath + "/v3/emails")
                .then().statusCode(403)
                .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{\"code\":\"403\",\"message\":\"This application is not allowed to send messages from the e-mail address from2@test.com\"}");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    @Test
    public void shouldCreateEmailEntityWithAttachment() throws Exception {
        given()
            .accept(ContentType.JSON)
            .request()
            .contentType(ContentType.JSON)
            .with().body("{ \"from\": \"test@mail.be\", \"to\": \"to2@test.com\", \"cc\": \"cc2@test.com, cc3 <cc3@test.com>\", \"html\": \"<html><body><p>My HTML message</p></body></html>\", \"replyTo\": \"replyTo2@test.com\", \"subject\": \"my_subject2\", \"tags\": [ { \"name\": \"my_tag2\", \"value\": \"my_value2\" } ], \"attachments\": [{ \"name\": \"my_attachment\", \"content\": \"abc123\", \"contentType\": \"text-plain\" }] } ")
            .when()
            .header(APPLICATION_ID_HEADER, APPLICATION_ID_0)
            .post("/" + resteasyPath + "/v3/emails")
            .then().statusCode(201);
    }

    @Test
    public void shouldNotCreateEmailEntityWithAttachmentWithMissingName() throws Exception {
        String body = given()
            .accept(ContentType.JSON)
            .request()
            .contentType(ContentType.JSON)
            .with().body("{ \"from\": \"servicefactory_email@digipolis.gent\", \"to\": \"to2@test.com\", \"cc\": \"cc2@test.com, cc3 <cc3@test.com>\", \"html\": \"<html><body><p>My HTML message</p></body></html>\", \"replyTo\": \"replyTo2@test.com\", \"subject\": \"my_subject2\", \"tags\": [ { \"name\": \"my_tag2\", \"value\": \"my_value2\" } ], \"attachments\": [{\"content\": \"abc123\", \"contentType\": \"text-plain\" }] } ")
            .when()
            .header(APPLICATION_ID_HEADER, APPLICATION_ID_0)
            .post("/" + resteasyPath + "/v3/emails")
            .then().statusCode(400)
            .extract().asString();
        JsonNode expected = new ObjectMapper().readTree("{ \"code\": \"400\", \"message\": \"Attachment name should not be null\" } ");
        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals(expected, json);
    }

    private void seedDatabase() {
        final String from0 = "from0@test.com";
        final String from1 = "from1@test.com";
        final String from2 = "from2@test.com";

        // Create EmailEntities
        EmailCreationDto dto0 = new EmailCreationDto();
        dto0.setSubject("my_subject0");
        dto0.setFrom(from0);
        dto0.setTo("to0@test.com");
        dto0.setText("my_text0");
        dto0.setCc("cc0@test.com");
        dto0.setBcc("bcc0@test.com");

        EmailCreationDto dto1 = new EmailCreationDto();
        dto1.setSubject("my_subject1");
        dto1.setFrom(from1);
        dto1.setTo("to1@test.com");
        dto1.setText("my_text1");
        dto1.setCc("cc1@test.com");
        dto1.setBcc("bcc1@test.com");

        emailEntityList = Arrays.asList(
                new EmailEntity(dto0, APPLICATION_ID_0),
                new EmailEntity(dto1, APPLICATION_ID_0)
        );
        emailEntityList.forEach((entity) -> entity.persist());
    }

    @Transactional
    public void clearDatabase() {
        EmailEntity.deleteAll();
    }

}