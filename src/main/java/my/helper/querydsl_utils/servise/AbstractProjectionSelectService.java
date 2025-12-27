package my.helper.querydsl_utils.servise;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.helper.querydsl_utils.servise.other.FilterGroup;
import my.helper.querydsl_utils.servise.other.FilterToPredicateMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public abstract class AbstractProjectionSelectService<E, D> extends AbstractSelectService {

    private final Expression<D> expression;
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityPathBase<E> entityPathBase;

    public AbstractProjectionSelectService(Map<String, ComparableExpressionBase<?>> fieldMap,
                                           Expression<D> expression, JPAQueryFactory jpaQueryFactory,
                                           EntityPathBase<E> entityPathBase) {

        super(fieldMap);
        this.expression = expression;
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityPathBase = entityPathBase;
    }

    public List<D> findAllByFilters(List<FilterGroup> filterGroups) {
        return findAllByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups));
    }

    public List<D> findAllByPredicate(List<Predicate> predicates) {
        return modifyQuery(jpaQueryFactory.select(expression).from(entityPathBase))
                .where(predicates.toArray(Predicate[]::new))
                .fetch();
    }

    public Page<D> getPageByFilters(List<FilterGroup> filterGroups, Pageable pageable) {
        return getPageByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups), pageable);
    }

    public Page<D> getPageByPredicate(List<Predicate> predicates, Pageable pageable) {
        JPAQuery<D> query = jpaQueryFactory.select(expression).from(entityPathBase)
                .where(predicates.toArray(Predicate[]::new));
        query = modifyQuery(query);

        return getPageByPredicate(entityPathBase, query, pageable);
    }

    public List<Map<String, Object>> findAllByFilters(List<String> fields, List<FilterGroup> filterGroups) {
        return findAllByPredicate(fields, FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups));
    }

    public List<Map<String, Object>> findAllByPredicate(List<String> fields, List<Predicate> predicates) {
        JPAQuery<Tuple> query = jpaQueryFactory.
                select(buildSelectExpressions(fields).toArray(Expression[]::new)).from(entityPathBase)
                .where(predicates.toArray(Predicate[]::new));
        List<Tuple> tuples = modifyQuery(query).fetch();

        return mapTupleToList(fields, tuples);
    }

    public Page<Map<String, Object>> getPageByFilters(List<String> fields, List<FilterGroup> filterGroups, Pageable pageable) {
        return getPageByPredicate(fields, FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups), pageable);
    }

    public Page<Map<String, Object>> getPageByPredicate(List<String> fields, List<Predicate> predicates, Pageable pageable) {

        JPAQuery<Tuple> query = jpaQueryFactory
                .select(buildSelectExpressions(fields).toArray(Expression[]::new))
                .from(entityPathBase)
                .where(predicates.toArray(Predicate[]::new));
        query = modifyQuery(query);

        return getPageByPredicate(entityPathBase, query, fields, pageable);
    }

    protected abstract <M> JPAQuery<M> modifyQuery(JPAQuery<M> query);
}
