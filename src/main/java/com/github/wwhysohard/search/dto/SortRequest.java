package com.github.wwhysohard.search.dto;

import com.github.wwhysohard.search.enums.SortOrder;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Used to build {@link javax.persistence.criteria.Order} using
 * {@link com.github.wwhysohard.search.utils.GenericCriteriaOrder}
 */
@Data
@Builder(setterPrefix = "with")
public class SortRequest {

    /**
     * The field by which sorting will be performed
     */
    @NotBlank(message = "FIELD_REQUIRED")
    private String field;

    /**
     * Indicates a direction by which the specified field will be sorted
     */
    private SortOrder order = SortOrder.ASC;

    /**
     * Field name is the substring of field, which starts from last dot if join path is provided.
     * The entire field otherwise.
     *
     * @return field name
     */
    public String getFieldName() {
        return field.substring(field.lastIndexOf('.') + 1);
    }

    /**
     * Join path is provided by field names separated by dot
     *
     * @return Full join path if exists, <code>null</code> otherwise
     */
    public String getJoin() {
        return field.contains(".") ? field.substring(0, field.lastIndexOf('.')) : null;
    }

}
