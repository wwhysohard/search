package io.github.wwhysohard.search.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class SearchRequest {

    @Valid
    private List<FilterRequest> filters;

    @Valid
    private List<SortRequest> sorts;

}
