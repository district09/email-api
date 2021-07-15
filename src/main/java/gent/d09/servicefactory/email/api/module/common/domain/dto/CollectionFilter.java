package gent.d09.servicefactory.email.api.module.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.QueryParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionFilter {
    @QueryParam("orderBy")
    private String orderBy;
    @QueryParam("filter")
    private String filter;
    @QueryParam("limit")
    private Integer limit;
    @QueryParam("offset")
    private Integer offset;
    @QueryParam("applicationId")
    private String applicationId;
}
