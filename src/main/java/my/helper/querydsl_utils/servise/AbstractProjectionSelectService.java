package my.helper.querydsl_utils.servise;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import my.helper.querydsl_utils.servise.other.FilterGroup;
import my.helper.querydsl_utils.servise.other.FilterToPredicateMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class AbstractProjectionSelectService<E, D> extends AbstractSelectService {

    private final Expression<D> expression;
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityPathBase<E> entityPathBase;

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

        Long total = query.clone().select(entityPathBase.count()).fetchOne();
        if (Objects.isNull(total)) total = 0L;

        List<D> content = new ArrayList<>();
        if (total != 0) {
            JPAQuery<D> tupleJPAQuery = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
            if (!pageable.getSort().isUnsorted()) {
                tupleJPAQuery = tupleJPAQuery.orderBy(getOrderSpecifiers(pageable));
            }
            content = tupleJPAQuery.fetch();
        }
        return new PageImpl<>(content, pageable, total);
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

        Long total = query.clone().select(entityPathBase.count()).fetchOne();
        if (Objects.isNull(total)) total = 0L;

        List<Map<String, Object>> content = new ArrayList<>();
        if (total != 0) {
            JPAQuery<Tuple> tupleJPAQuery = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
            if (!pageable.getSort().isUnsorted()) {
                tupleJPAQuery = tupleJPAQuery.orderBy(getOrderSpecifiers(pageable));
            }
            content = mapTupleToList(fields, tupleJPAQuery.fetch());
        }
        return new PageImpl<>(content, pageable, total);
    }

    protected abstract <M> JPAQuery<M> modifyQuery(JPAQuery<M> query);
}
