package io.github.wwhysohard.search.dto;

import io.github.wwhysohard.search.enums.QueryOperator;
import io.github.wwhysohard.search.utils.GenericCriteriaPredicate;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Used to build {@link javax.persistence.criteria.Predicate} using
 * {@link GenericCriteriaPredicate}
 */
@Data
@Builder(setterPrefix = "with")
public class FilterRequest {

    /**
     * Indicates an operation applied to the specified field or collected list of filters
     */
    @NotNull
    private final QueryOperator operator;

    /**
     * The field on which the operation will be performed
     */
    private String field;

    /**
     * The value with which the specified operation will be performed on the specified field
     */
    private String value;

    /**
     * The values with which the specified operation will be performed on the specified field
     */
    private List<String> values;

    /**
     * List of filters which will be applied while filtering and collected by the specified operator
     */
    @Valid
    private List<FilterRequest> filters;

    /**
     * Join path is provided by field names separated by dot
     *
     * @return Full join path if exists, <code>null</code> otherwise
     */
    public String getJoin() {
        return field.contains(".") ? field.substring(0, field.lastIndexOf('.')) : null;
    }

    /**
     * Field name is the substring of field, which starts from last dot if join path is provided.
     * The entire field otherwise.
     *
     * @return field name
     */
    public String getFieldName() {
        return field.substring(field.lastIndexOf('.') + 1);
    }

}
