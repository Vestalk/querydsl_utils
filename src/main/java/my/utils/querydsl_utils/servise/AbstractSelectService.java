package my.utils.querydsl_utils.servise;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import my.utils.querydsl_utils.servise.other.field.FieldInfo;
import my.utils.querydsl_utils.servise.other.filter.FilterGroup;
import my.utils.querydsl_utils.servise.other.filter.FilterToPredicateMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractSelectService {

    private final Map<String, FieldInfo> FIELD_MAP;

    public AbstractSelectService(Map<String, FieldInfo> fieldInfos) {
        this.FIELD_MAP = fieldInfos;
    }

    protected <E, T> Page<T> getPageByPredicate(EntityPathBase<E> entityPathBase, JPAQuery<T> query,
                                                Pageable pageable) {

        Long total = query.clone().select(entityPathBase.count()).fetchOne();
        if (Objects.isNull(total)) total = 0L;

        List<T> content = new ArrayList<>();
        if (total != 0) {
            query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
            if (!pageable.getSort().isUnsorted()) {
                query = query.orderBy(getOrderSpecifiers(pageable));
            }
            content = query.fetch();
        }
        return new PageImpl<>(content, pageable, total);
    }

    protected <E> Page<Map<String, Object>> getPageByPredicate(
            EntityPathBase<E> entityPathBase, JPAQuery<Tuple> query,
            List<String> fields, Pageable pageable) {

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

    protected List<Map<String, Object>> mapTupleToList(List<String> fields, List<Tuple> tuples) {
        return tuples.stream()
                .map(tuple -> fields
                        .stream()
                        .filter(getFieldMap()::containsKey)
                        .collect(Collectors.toMap(
                                Function.identity(),
                                field -> (Object) tuple.get(getFieldMap().get(field).getPath())
                        )))
                .toList();
    }

    protected Expression<?> getSelectExpressions(String field) {
        if (getFieldMap().containsKey(field)) {
            return getFieldMap().get(field).getPath();
        }
        throw new RuntimeException("Unsupported field: " + field);
    }

    protected List<? extends Expression<?>> buildSelectExpressions(List<String> fields) {
        return fields.stream()
                .map(getFieldMap()::get)
                .map(FieldInfo::getPath)
                .filter(Objects::nonNull)
                .toList();
    }

    protected OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> list = pageable.getSort()
                .stream()
                .map(order -> {
                    ComparableExpressionBase<?> path = getFieldMap().get(order.getProperty()).getPath();
                    return Sort.Direction.ASC.equals(order.getDirection()) ? path.asc() : path.desc();
                })
                .toList();
        return list.toArray(new OrderSpecifier<?>[0]);
    }

    public Map<String, FieldInfo> getFieldMap() {
        return FIELD_MAP;
    }

    public List<?> findDistinctFieldValuesByFilterGroups(String field, List<FilterGroup> filterGroups) {
        return findDistinctFieldValuesByPredicates(
                field, FilterToPredicateMapper.getPredicates(getFieldMap(), filterGroups));
    }

    public abstract List<?> findDistinctFieldValuesByPredicates(String field, List<Predicate> predicates);

    public abstract String getMasterType();
}
