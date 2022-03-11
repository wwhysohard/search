package io.github.wwhysohard.search.specification;

import io.github.wwhysohard.search.dto.FilterRequest;
import io.github.wwhysohard.search.dto.SearchRequest;
import io.github.wwhysohard.search.dto.SortRequest;
import io.github.wwhysohard.search.enums.QueryOperator;
import io.github.wwhysohard.search.utils.GenericCriteriaOrder;
import io.github.wwhysohard.search.utils.GenericCriteriaPredicate;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base {@link Specification} class
 *
 * @param <T> generic type of model
 */
public abstract class GenericSpecification<T> implements Specification<T> {

    private final SearchRequest request;
    private final Class<T> genericType;

    protected final Map<String, Join<?, ?>> joins;
    protected final List<Predicate> predicates;

    /**
     * Constructs {@link GenericSpecification}
     *
     * @param request {@link SearchRequest} with filters and sorts
     * @param genericType {@link Class} instance of a model
     */
    protected GenericSpecification(SearchRequest request, Class<T> genericType) {
        this.request = request;
        this.genericType = genericType;

        this.joins = new HashMap<>();
        this.predicates = new ArrayList<>();
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        initializeJoins(root);
        processAccess(root, criteriaBuilder);
        filter(root, criteriaBuilder);
        sort(root, query, criteriaBuilder);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * Override and initialize joins if necessary
     *
     * @param root {@link Root} of the model
     */
    protected void initializeJoins(Root<T> root) {}

    /**
     * Override and process access if necessary
     *
     * @param root {@link Root} of the model
     * @param criteriaBuilder {@link CriteriaBuilder} which may be needed for access operations
     */
    protected void processAccess(Root<T> root, CriteriaBuilder criteriaBuilder) {}

    /**
     * Constructs {@link Predicate} using provided filters and adds it into <code>predicates</code>
     *
     * @param root {@link Root} of the model on which filtering will be processed
     * @param criteriaBuilder {@link CriteriaBuilder} which will be used to construct {@link Predicate}
     */
    private void filter(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<FilterRequest> filters = request.getFilters();

        if (filters != null && !filters.isEmpty()) {
            Predicate predicate = GenericCriteriaPredicate.get(root, criteriaBuilder, joins, filters, QueryOperator.AND, genericType);
            predicates.add(predicate);
        }
    }

    /**
     * Collects {@link List} of {@link Order}s and sorts result
     *
     * @param root {@link Root} of the model on which filtering will be processed
     * @param query {@link CriteriaQuery} which will be used to sort result
     * @param criteriaBuilder {@link CriteriaBuilder} which will be used to construct {@link Order}s
     */
    private void sort(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<SortRequest> sorts = request.getSorts();

        if (sorts != null && !sorts.isEmpty()) {
            List<Order> orders = GenericCriteriaOrder.get(root, criteriaBuilder, joins, sorts, genericType);
            query.orderBy(orders);
        }
    }

}
