package gent.d09.servicefactory.email.api.module.email.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    private Long id;
    private String name;
    private String content;
    private String contentType;
}
