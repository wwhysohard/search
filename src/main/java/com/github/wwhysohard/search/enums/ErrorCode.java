package com.github.wwhysohard.search.enums;

/**
 * Indicates error message for {@link com.github.wwhysohard.search.exception.FilterException} occurrence
 */
public enum ErrorCode {

    /**
     * Indicates that <code>value</code> or <code>values</code> passed via {@link com.github.wwhysohard.search.dto.FilterRequest}
     * do not match <code>type</code> of the specified <code>field</code>
     */
    ILLEGAL_ARGUMENT,

    /**
     * Indicates that <code>value</code> of {@link com.github.wwhysohard.search.dto.FilterRequest} cannot be <code>null</code> in this context
     */
    VALUE_CANNOT_BE_NULL,

    /**
     * Indicates that <code>values</code> of {@link com.github.wwhysohard.search.dto.FilterRequest} cannot be <code>null</code> in this context
     */
    VALUES_CANNOT_BE_NULL,

    /**
     * Indicates that <code>operator</code> of {@link com.github.wwhysohard.search.dto.FilterRequest} is illegal in this context
     */
    ILLEGAL_OPERATOR,

    /**
     * Indicates that the specified field is not annotated with @{@link com.github.wwhysohard.search.annotation.Filterable},
     * or it's not included in <code>names</code> of @{@link com.github.wwhysohard.search.annotation.Filterable},
     * or, if the specified field is in JPA relation and <code>joinable</code> of @{@link com.github.wwhysohard.search.annotation.Filterable}
     * is set to <code>false</code>
     */
    FIELD_IS_NOT_ALLOWED_FOR_FILTERING,

    /**
     * Indicates that <code>field</code> of {@link com.github.wwhysohard.search.dto.FilterRequest} cannot be <code>null</code> in this context
     */
    FIELD_CANNOT_BE_NULL,

    /**
     * Indicates that <code>filters</code> of {@link com.github.wwhysohard.search.dto.FilterRequest} cannot be neither null nor empty in this context
     */
    FILTERS_CANNOT_BE_EMPTY,

    INVALID_SORTING_FIELD,

}
