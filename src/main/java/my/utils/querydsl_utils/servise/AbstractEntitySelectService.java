package my.utils.querydsl_utils.servise;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.utils.querydsl_utils.servise.other.FieldInfo;
import my.utils.querydsl_utils.servise.other.FilterGroup;
import my.utils.querydsl_utils.servise.other.FilterToPredicateMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public abstract class AbstractEntitySelectService<T> extends AbstractSelectService {

    private final EntityPathBase<T> entityPathBase;
    private final JPAQueryFactory jpaQueryFactory;

    protected AbstractEntitySelectService(Map<String, FieldInfo> fieldInfos,
                                          EntityPathBase<T> entityPathBase, JPAQueryFactory jpaQueryFactory) {

        super(fieldInfos);
        this.entityPathBase = entityPathBase;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<?> findDistinctFieldValues(String field) {
        return jpaQueryFactory
                .select(getSelectExpressions(field))
                .distinct()
                .from(entityPathBase)
                .fetch();
    }

    public List<T> findAllByFilters(List<FilterGroup> filterGroups) {
        return findAllByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups));
    }

    public List<T> findAllByPredicate(List<Predicate> predicates) {
        return jpaQueryFactory.select(entityPathBase)
                .from(entityPathBase)
                .where(predicates.toArray(Predicate[]::new))
                .fetch();
    }

    public Page<T> getPageByFilters(List<FilterGroup> filterGroups, Pageable pageable) {
        return getPageByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups), pageable);
    }

    public Page<T> getPageByPredicate(List<Predicate> predicates, Pageable pageable) {
        JPAQuery<T> query = jpaQueryFactory.select(entityPathBase)
                .from(entityPathBase).where(predicates.toArray(Predicate[]::new));

        return getPageByPredicate(entityPathBase, query, pageable);
    }

    public List<Map<String, Object>> findAllByFilters(List<String> fields, List<FilterGroup> filterGroups) {
        return findAllByPredicate(fields, FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups));
    }

    public List<Map<String, Object>> findAllByPredicate(List<String> fields, List<Predicate> predicates) {
        List<Tuple> tuples = jpaQueryFactory
                .select(buildSelectExpressions(fields).toArray(Expression[]::new))
                .from(entityPathBase)
                .where(predicates.toArray(Predicate[]::new))
                .fetch();

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

        return getPageByPredicate(entityPathBase, query, fields, pageable);
    }
}
