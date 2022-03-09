package com.github.wwhysohard.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated field will be allowed for filtering by
 * {@link com.github.wwhysohard.search.utils.GenericCriteriaPredicate}
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Filterable {

    /**
     * Used on relationships between models.
     * If set to "true" then @{@link Filterable} recursion will be applied
     * on related object's fields. Filtration will not be allowed otherwise.
     */
    boolean joinable() default false;

    /**
     * Defines names associated with the given field.
     * By default, only field name will be checked for @{@link Filterable}
     */
    String[] names() default {};

}
