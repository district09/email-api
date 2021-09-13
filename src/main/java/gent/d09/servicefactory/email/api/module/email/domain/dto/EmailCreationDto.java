package gent.d09.servicefactory.email.api.module.email.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gent.d09.servicefactory.email.api.module.email.domain.Attachment;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailCreationDto {
    public String from;
    public String to;
    public String cc;
    public String bcc;
    public String replyTo;
    public String subject;
    public String text;
    public String html;
    public List<Attachment> attachments;
    public List<Attachment> inlineImages;
    public Boolean async;
}
