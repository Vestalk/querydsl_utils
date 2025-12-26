package my.helper.querydsl_utils.servise;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractSelectService {

    protected List<Map<String, Object>> map(List<String> fields, List<Tuple> tuples) {
        return tuples.stream()
                .map(tuple -> fields
                        .stream()
                        .filter(getFieldMap()::containsKey)
                        .collect(Collectors.toMap(
                                Function.identity(),
                                field -> (Object) tuple.get(getFieldMap().get(field))
                        )))
                .toList();
    }

    protected List<? extends Expression<?>> buildSelectExpressions(List<String> fields) {
        return fields.stream()
                .map(getFieldMap()::get)
                .filter(Objects::nonNull)
                .toList();
    }

    protected OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return (OrderSpecifier<?>[]) pageable.getSort()
                .stream()
                .map(order -> {
                    ComparableExpressionBase<?> path = getFieldMap().get(order.getProperty());
                    return Sort.Direction.ASC.equals(order.getDirection()) ? path.asc() : path.desc();
                })
                .toArray();
    }

    protected abstract Map<String, ComparableExpressionBase<?>> getFieldMap();

}
