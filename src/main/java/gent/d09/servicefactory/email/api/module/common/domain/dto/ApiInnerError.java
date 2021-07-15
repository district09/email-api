package gent.d09.servicefactory.email.api.module.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiInnerError {
    private String code;
    private ApiInnerError innerError;

    protected ApiInnerError(){
    }

    public static ApiInnerError of(String code){
        ApiInnerError res = new ApiInnerError();
        res.code = code;
        return res;
    }

    public static ApiInnerError of(String code, ApiInnerError apiInnerError){
        ApiInnerError res = of(code);
        res.innerError = apiInnerError;
        return res;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ApiInnerError getInnerError() {
        return innerError;
    }

    public void setInnerError(ApiInnerError innerError) {
        this.innerError = innerError;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
