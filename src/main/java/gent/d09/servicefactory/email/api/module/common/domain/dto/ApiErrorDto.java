package gent.d09.servicefactory.email.api.module.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorDto {
    private ApiError error;

    public static ApiErrorDto of(Exception ex, String status, HttpServletRequest request){
        ApiErrorDto apiErrorDto = new ApiErrorDto();
        apiErrorDto.error = ApiError.of(ex, status, request);
        return apiErrorDto;
    }
}
