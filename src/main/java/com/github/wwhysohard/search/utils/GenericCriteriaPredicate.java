package com.github.wwhysohard.search.utils;

import com.github.wwhysohard.search.dto.FilterRequest;
import com.github.wwhysohard.search.enums.ErrorCode;
import com.github.wwhysohard.search.enums.QueryOperator;
import com.github.wwhysohard.search.exception.FilterException;
import com.github.wwhysohard.search.validator.FilterableValidator;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class which constructs {@link Predicate} by provided filters
 */
public class GenericCriteriaPredicate {

    /**
     * Creating an instance of {@link GenericCriteriaPredicate} is illegal
     */
    private GenericCriteriaPredicate() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Constructs {@link Predicate}s for the given {@link Root} or {@link Join} by provided filters
     * and collects them by the specified operation
     *
     * @param root {@link Root} of the model on which filtering will be processed
     * @param criteriaBuilder {@link CriteriaBuilder} which will be used to construct {@link Predicate}
     * @param joins {@link Map} of {@link Join}s from the given model on which filtering is allowed
     * @param filters filters to be applied on the model to construct {@link Predicate}
     * @param operator operator by which {@link Predicate}s will be collected
     * @param clazz {@link Class} instance of the model
     * @param <T> generic type of the model
     *
     * @return {@link Predicate} constructed for the given {@link Root} or {@link Join}
     * by provided filters and collected by the specified <code>operator</code>
     */
    public static <T> Predicate get(Root<T> root, CriteriaBuilder criteriaBuilder,
                                    Map<String, Join<?, ?>> joins, List<FilterRequest> filters,
                                    QueryOperator operator, Class<T> clazz) {
        List<Predicate> predicates = new ArrayList<>();

        for (FilterRequest filter : filters) {
            predicates.add(get(root, criteriaBuilder, joins, filter, clazz));
        }

        switch (operator) {
            case AND:
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            case OR:
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            default:
                throw new FilterException(ErrorCode.ILLEGAL_OPERATOR);
        }
    }

    /**
     * Either constructs {@link Predicate} for the given {@link Root} or {@link Join} or calls overloaded method
     *
     * @param root {@link Root} of the model on which filtering will be processed
     * @param criteriaBuilder {@link CriteriaBuilder} which will be used to construct {@link Predicate}
     * @param joins {@link Map} of {@link Join}s from the given model on which filtering is allowed
     * @param filter filter to be applied on the model to construct {@link Predicate}
     * @param clazz {@link Class} instance of the model
     * @param <T> generic type of the model
     *
     * @return {@link Predicate} constructed for the given {@link Root} or {@link Join}
     */
    private static <T> Predicate get(Root<T> root, CriteriaBuilder criteriaBuilder,
                                     Map<String, Join<?, ?>> joins, FilterRequest filter,
                                     Class<T> clazz) {
        if (filter.getOperator() == QueryOperator.OR || filter.getOperator() == QueryOperator.AND) {
            validateFilters(filter.getFilters());
            return get(root, criteriaBuilder, joins, filter.getFilters(), filter.getOperator(), clazz);
        }

        validateField(filter.getField());
        validateIsFieldFilterable(clazz, filter.getField());

        if (filter.getJoin() == null) {
            return get(root, criteriaBuilder, filter);
        } else {
            return get(joins.get(filter.getJoin()), criteriaBuilder, filter);
        }
    }

    /**
     * Constructs and returns {@link Predicate} on provided {@link From} by the specified <code>filter</code>
     *
     * @param from {@link From} on which <code>filter</code> will be applied
     * @param criteriaBuilder {@link CriteriaBuilder} which will be used to construct {@link Predicate}
     * @param filter filter by which {@link Predicate} will be constructed
     *
     * @return {@link Predicate} constructed by the given <code>filter</code>
     *
     * @throws FilterException with <code>ILLEGAL_OPERATOR</code> if the specified <code>operator</code> is not allowed
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <X, Y> Predicate get(From<X, Y> from, CriteriaBuilder criteriaBuilder, FilterRequest filter) {
        validateFrom(from);
        switch (filter.getOperator()) {
            case EQUALS:
                return criteriaBuilder.equal(from.get(filter.getFieldName()),
                        cast(from.get(filter.getFieldName()).getJavaType(), filter.getValue()));
            case NOT_EQUALS:
                return criteriaBuilder.notEqual(from.get(filter.getFieldName()),
                        cast(from.get(filter.getFieldName()).getJavaType(), filter.getValue()));
            case LESS_THAN:
                return criteriaBuilder.lessThan(from.get(filter.getFieldName()),
                        (Comparable) cast(from.get(filter.getFieldName()).getJavaType(), filter.getValue()));
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(from.get(filter.getFieldName()),
                        (Comparable) cast(from.get(filter.getFieldName()).getJavaType(), filter.getValue()));
            case LESS_THAN_OR_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(from.get(filter.getFieldName()),
                        (Comparable) cast(from.get(filter.getFieldName()).getJavaType(), filter.getValue()));
            case GREATER_THAN_OR_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(from.get(filter.getFieldName()),
                        (Comparable) cast(from.get(filter.getFieldName()).getJavaType(), filter.getValue()));
            case LIKE:
                return criteriaBuilder.like(from.get(filter.getFieldName()), "%" + filter.getValue() + "%");
            case IN:
                return from.get(filter.getFieldName()).in(cast(from.get(filter.getFieldName()).getJavaType(), filter.getValues()));
            case NULL:
                return criteriaBuilder.isNull(from.get(filter.getFieldName()));
            case NOT_NULL:
                return criteriaBuilder.isNotNull(from.get(filter.getFieldName()));
            default:
                throw new FilterException(ErrorCode.ILLEGAL_OPERATOR);
        }
    }

    /**
     * Casts the <code>value</code> to required type
     *
     * @param clazz {@link Class} type to which the value is to be cast
     * @param value value to be cast
     *
     * @return <code>value</code> cast to the type of {@link Class}
     *
     * @throws FilterException with <code>ILLEGAL_ARGUMENT</code> {@link ErrorCode}
     * if value cannot be cast to the given type
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object cast(Class clazz, String value) {
        validateValue(value);

        try {
            if (clazz.isAssignableFrom(String.class)) {
                return value;
            } else if (Enum.class.isAssignableFrom(clazz)) {
                return Enum.valueOf(clazz, value);
            } else if (clazz.isAssignableFrom(LocalDateTime.class)) {
                return LocalDateTime.parse(value);
            } else if (clazz.isAssignableFrom(LocalDate.class)) {
                return LocalDate.parse(value);
            } else if (clazz.isAssignableFrom(LocalTime.class)) {
                return LocalTime.parse(value);
            } else if (clazz.isAssignableFrom(long.class) || clazz.isAssignableFrom(Long.class)) {
                return Long.valueOf(value);
            } else if (clazz.isAssignableFrom(double.class) || clazz.isAssignableFrom(Double.class)) {
                return Double.valueOf(value);
            } else if (clazz.isAssignableFrom(int.class) || clazz.isAssignableFrom(Integer.class)) {
                return Integer.valueOf(value);
            } else if (clazz.isAssignableFrom(boolean.class) || clazz.isAssignableFrom(Boolean.class)) {
                return Boolean.valueOf(value);
            } else if (clazz.isAssignableFrom(short.class) || clazz.isAssignableFrom(Short.class)) {
                return Short.valueOf(value);
            } else if (clazz.isAssignableFrom(byte.class) || clazz.isAssignableFrom(Byte.class)) {
                return Byte.valueOf(value);
            } else if (clazz.isAssignableFrom(float.class) || clazz.isAssignableFrom(Float.class)) {
                return Float.valueOf(value);
            } else if (value.length() > 0 && (clazz.isAssignableFrom(char.class) || clazz.isAssignableFrom(Character.class))) {
                return value.charAt(0);
            }
        } catch (RuntimeException e) {
            throw new FilterException(ErrorCode.ILLEGAL_ARGUMENT);
        }

        throw new FilterException(ErrorCode.ILLEGAL_ARGUMENT);
    }

    /**
     * Casts <code>values</code> to required type
     *
     * @param clazz {@link Class} type to which the value is to be cast
     * @param values values to be cast
     *
     * @return <code>values</code> cast to the type of {@link Class}
     */
    private static List<Object> cast(Class<?> clazz, List<String> values) {
        validateValues(values);
        return values.stream().map(v -> cast(clazz, v)).collect(Collectors.toList());
    }

    /**
     * Validates that provided <code>value</code> is NOT <code>null</code>
     *
     * @param value value to be validated
     *
     * @throws FilterException with <code>VALUE_CANNOT_BE_NULL</code> {@link ErrorCode}
     * if value is <code>null</code>
     */
    private static void validateValue(String value) {
        if (value == null) {
            throw new FilterException(ErrorCode.VALUE_CANNOT_BE_NULL);
        }
    }

    /**
     * Validates that provided <code>values</code> is NOT <code>null</code>
     *
     * @param values values to be validated
     *
     * @throws FilterException with <code>VALUES_CANNOT_BE_NULL</code> {@link ErrorCode}
     * if <code>values</code> is <code>null</code>
     */
    private static void validateValues(List<String> values) {
        if (values == null) {
            throw new FilterException(ErrorCode.VALUES_CANNOT_BE_NULL);
        }
    }

    /**
     * Validates that provided {@link From} is NOT <code>null</code>
     *
     * @param from {@link From} instance to be validated
     *
     * @throws FilterException with <code>FIELD_IS_NOT_ALLOWED_FOR_FILTERING</code> {@link ErrorCode}
     * if <code>from</code> is <code>null</code>
     */
    private static <X, Y> void validateFrom(From<X, Y> from) {
        if (from == null) {
            throw new FilterException(ErrorCode.FIELD_IS_NOT_ALLOWED_FOR_FILTERING);
        }
    }

    /**
     * Validates that provided <code>field</code> is NOT <code>null</code>
     *
     * @param field field to be validated
     *
     * @throws FilterException with <code>FIELD_CANNOT_BE_NULL</code> {@link ErrorCode}
     * if <code>field</code> is <code>null</code>
     */
    private static void validateField(String field) {
        if (field == null) {
            throw new FilterException(ErrorCode.FIELD_CANNOT_BE_NULL);
        }
    }

    /**
     * Validates that provided list of <code>filters</code> is neither NOT <code>null</code> nor empty
     *
     * @param filters {@link List} of {@link FilterRequest}s to be validated
     *
     * @throws FilterException with <code>FILTERS_CANNOT_BE_EMPTY</code> {@link ErrorCode}
     * if <code>filters</code> are <code>null</code> or empty
     */
    private static void validateFilters(List<FilterRequest> filters) {
        if (filters == null || filters.isEmpty()) {
            throw new FilterException(ErrorCode.FILTERS_CANNOT_BE_EMPTY);
        }
    }

    /**
     * Validates that the specified field is allowed for filtering
     *
     * @param clazz {@link Class} instance of the model
     * @param field field to be validated
     * @param <T> generic type of the model
     *
     * @throws FilterException with <code>FIELD_IS_NOT_ALLOWED_FOR_FILTERING</code> {@link ErrorCode}
     * if the specified field is not allowed for filtering
     */
    private static <T> void validateIsFieldFilterable(Class<T> clazz, String field) {
        if (!FilterableValidator.isValid(clazz, field)) {
            throw new FilterException(ErrorCode.FIELD_IS_NOT_ALLOWED_FOR_FILTERING);
        }
    }

}
