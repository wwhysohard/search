package com.github.wwhysohard.search.utils;

import com.github.wwhysohard.search.dto.SortRequest;
import com.github.wwhysohard.search.enums.ErrorCode;
import com.github.wwhysohard.search.exception.FilterException;
import com.github.wwhysohard.search.validator.FilterableValidator;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class which constructs {@link List} of {@link Order}s by provided sorts
 */
public class GenericCriteriaOrder {

    /**
     * Creating an instance of {@link GenericCriteriaOrder} is illegal
     */
    private GenericCriteriaOrder() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param root {@link Root} of the model on which filtering will be processed
     * @param criteriaBuilder {@link CriteriaBuilder} which will be used to construct {@link Order}s
     * @param joins {@link Map} of {@link Join}s from the given model on which sorting is allowed
     * @param sorts sorts to be applied on the model to construct {@link Order}s
     * @param clazz {@link Class} instance of the model
     * @param <T> generic type of the model
     *
     * @return {@link List} of {@link Order}s by which sorting will be applied
     */
    public static <T> List<Order> get(Root<T> root, CriteriaBuilder criteriaBuilder,
                                      Map<String, Join<?, ?>> joins, List<SortRequest> sorts, Class<T> clazz) {
        List<Order> orders = new ArrayList<>();

        for (SortRequest sort : sorts) {
            validateFieldSorting(clazz, sort.getField());

            switch (sort.getOrder()) {
                case ASC:
                    orders.add(criteriaBuilder.asc(getFrom(root, joins, sort).get(sort.getFieldName())));
                    break;
                case DESC:
                    orders.add(criteriaBuilder.desc(getFrom(root, joins, sort).get(sort.getFieldName())));
                    break;
            }
        }

        return orders;
    }

    /**
     * Validated that the specified field is allowed for sorting
     *
     * @param clazz {@link Class} instance of the model
     * @param field field to be validated
     * @param <T> generic type of the model
     */
    private static <T> void validateFieldSorting(Class<T> clazz, String field) {
        if (!FilterableValidator.isValid(clazz, field)) {
            throw new FilterException(ErrorCode.INVALID_SORTING_FIELD);
        }
    }

    /**
     * Returns passed {@link Root} if there's no join path, calls <code>getJoin</code> method otherwise
     *
     * @param root {@link Root} of the model on which sorting will be processed
     * @param joins {@link Map} of {@link Join}s from the given model on which sorting is allowed
     * @param sort sort to be applied on the model to construct {@link Order}
     * @param <T> generic type of the model
     *
     * @return {@link From} instance which is either {@link Root} passed as parameter or {@link Join} from map
     */
    private static <T> From<?, ?> getFrom(Root<T> root, Map<String, Join<?, ?>> joins, SortRequest sort) {
        return (sort.getJoin() != null) ? getJoin(joins, sort.getJoin()) : root;
    }

    /**
     * Returns {@link Join} from provided {@link Map} of joins
     *
     * @param joins {@link Map} which keys are join names
     * @param joinName name of join to be returned
     *
     * @return {@link Join} by the specified <code>joinName</code>
     * @throws FilterException with <code>INVALID_SORTING_FIELD</code> {@link ErrorCode}
     * if map does not contain required {@link Join}
     */
    private static Join<?, ?> getJoin(Map<String, Join<?, ?>> joins, String joinName) {
        Join<?, ?> join = joins.get(joinName);
        if (join == null) throw new FilterException(ErrorCode.INVALID_SORTING_FIELD);
        return join;
    }

}
