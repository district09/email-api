package gent.d09.servicefactory.email.api.module.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import gent.d09.servicefactory.email.api.module.common.util.ErrorUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String code;
    private String message;
    private String target;
    private List<ApiErrorDetail> details;
    private ApiInnerError innerError;

    public static ApiError of(Exception ex, String code, HttpServletRequest request){
        ApiError apiError = new ApiError();

        if(ex instanceof WebApplicationException){
            apiError.message = ex.getMessage();
            apiError.code = ((WebApplicationException) ex).getResponse().getStatus() + "";
        }
        else {
            apiError.message = ErrorUtils.containsStackTrace(ex.getMessage())? "" : ex.getMessage(); // Filter stack traces
        }
        if(ex.getCause() != null && !ErrorUtils.containsStackTrace(ex.getCause().getMessage())){ // Filter stack traces
            apiError.details = Collections.singletonList(
                    ApiErrorDetail.builder()
                        .code(code)
                        .target(request != null ? request.getRequestURI() : null)
                        .message(ex.getCause().getMessage())
                        .build());
        }

        apiError.target = request != null ? request.getRequestURI(): null;


        return apiError;
    }

    public static ApiError of(String message, String code, HttpServletRequest request){
        ApiError apiError = new ApiError();
        apiError.message = message;
        apiError.target = request != null ? request.getRequestURI() : null;
        apiError.code = code;
        return apiError;
    }
}
