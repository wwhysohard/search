package io.github.wwhysohard.search.enums;

/**
 * Indicates the operation applied to the filtering
 */
public enum QueryOperator {

    /**
     * The specified <code>field</code> must be equal to the specified <code>value</code>
     */
    EQUALS,

    /**
     * The specified <code>field</code> must NOT be equal to the specified <code>value</code>
     */
    NOT_EQUALS,

    /**
     * The specified <code>field</code> must be less than the specified <code>value</code>
     */
    LESS_THAN,

    /**
     * The specified <code>field</code> must be greater than the specified <code>value</code>
     */
    GREATER_THAN,

    /**
     * The specified <code>field</code> must be less than OR equal to the specified <code>value</code>
     */
    LESS_THAN_OR_EQUAL,

    /**
     * The specified <code>field</code> must be greater than OR equal to the specified <code>value</code>
     */
    GREATER_THAN_OR_EQUAL,

    /**
     * SQL like operation will be applied to specified field
     */
    LIKE,

    /**
     * The specified <code>values</code> must contain the specified <code>field</code>
     */
    IN,

    /**
     * The specified <code>field</code> value must not occur in the specified <code>values</code>
     */
    NOT_IN,

    /**
     * The specified <code>field</code> must be <code>null</code>
     */
    NULL,

    /**
     * The specified <code>field</code> must NOT be <code>null</code>
     */
    NOT_NULL,

    /**
     * OR operation will be applied on the specified <code>filters</code>
     */
    OR,

    /**
     * AND operation will be applied on the specified <code>filters</code>
     */
    AND

}
