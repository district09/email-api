package gent.d09.servicefactory.email.api.module.email.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDto {
    private Long id;
    private String status;
    private String statusMessage;
}
