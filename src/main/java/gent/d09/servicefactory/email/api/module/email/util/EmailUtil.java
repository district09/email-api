package gent.d09.servicefactory.email.api.module.email.util;

import gent.d09.servicefactory.email.api.module.email.domain.Attachment;
import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailCreationDto;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpStatus;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtil {
    private static final Pattern emailPattern = Pattern.compile("(.*)[<](.+@.+\\..+)[>]");

    public static void validateDto(EmailCreationDto dto) {
        if(dto == null) throw new WebApplicationException("EmailCreationDto should not be null", HttpStatus.SC_BAD_REQUEST);
        if(dto.getFrom() == null || dto.getFrom().length() == 0) throw new WebApplicationException("From should not be null", HttpStatus.SC_BAD_REQUEST);
        if(dto.getTo() == null || dto.getTo().length() == 0) throw new WebApplicationException("To should not be null", HttpStatus.SC_BAD_REQUEST);

        validateEmailField(dto.getFrom());
        if(splitCommaSeparatedEmails(dto.getFrom()).size() != 1){
            throw new WebApplicationException("From should contain a single e-mail address", HttpStatus.SC_BAD_REQUEST);
        }

        validateEmailField(dto.getTo());
        validateEmailField(dto.getBcc());
        validateEmailField(dto.getCc());

        validateAttachments(dto.getAttachments());
        validateAttachments(dto.getInlineImages());
    }

    public static List<String> splitCommaSeparatedEmails(String csv){
        if(csv == null){
            return new ArrayList<>();
        }
        return Arrays.asList(csv.split(","));

        //List<String> emails = new ArrayList<>();
        /*Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(csv);
        while (m.find()) {
            emails.add(m.group());
        }
        return emails;*/
    }

    private static void validateEmailField(String csv){
        if(csv == null){
            return;
        }
        List<String> emails = splitCommaSeparatedEmails(csv);
        if(emails.isEmpty() && csv.length() > 0){
            throw new WebApplicationException(csv + " is not a valid e-mail address list", HttpStatus.SC_BAD_REQUEST);
        }
        if(emails.size() < csv.split("@").length - 1){
            throw new WebApplicationException(csv + " is not a valid e-mail address list", HttpStatus.SC_BAD_REQUEST);
        }
        emails.forEach(email -> {
            email = getRawEmailAddress(email);
            if(!EmailValidator.getInstance().isValid(email)) {
                throw new WebApplicationException(email + " is not a valid e-mail address", HttpStatus.SC_BAD_REQUEST);
            }
        });
    }

    private static void validateAttachments(List<Attachment> dtos) {
        if(dtos == null) {
            return;
        }
        for(Attachment dto: dtos) {
            if(dto.getContent() == null) {
                throw new WebApplicationException("Attachment content should not be null", HttpStatus.SC_BAD_REQUEST);
            }
            if(dto.getContentType() == null) {
                throw new WebApplicationException("Attachment contentType should not be null", HttpStatus.SC_BAD_REQUEST);
            }
            if(dto.getName() == null) {
                throw new WebApplicationException("Attachment name should not be null", HttpStatus.SC_BAD_REQUEST);
            }
            if(dto.getId() == null) {
                dto.setId(Math.abs(new Random().nextLong())); // set ID if not provided
            }
        }
    }

    public static String getRawEmailAddress(String email) {
        Matcher matcher = emailPattern.matcher(email);
        if(matcher.find()) {
            return matcher.group(2);
        }
        return email;
    }
}
