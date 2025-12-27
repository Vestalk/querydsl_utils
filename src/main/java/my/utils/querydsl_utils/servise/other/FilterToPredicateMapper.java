package my.utils.querydsl_utils.servise.other;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class FilterToPredicateMapper {

    public static List<Predicate> getPredicates(Map<String, ComparableExpressionBase<?>> fieldMap,
                                                List<FilterGroup> filterGroups) {

        return filterGroups.stream()
                .map(group -> buildPredicates(fieldMap, group))
                .toList();
    }

    private static Predicate buildPredicates(Map<String, ComparableExpressionBase<?>> fieldMap,
                                             FilterGroup filterGroup) {

        return filterGroup.getFilters()
                .stream()
                .filter(filter -> fieldMap.containsKey(filter.getField()))
                .map(filter -> {
                    Path<?> path = (Path<?>) fieldMap.get(filter.getField());

                    if (FilterType.EQUALS.equals(filter.getFilterType())) {
                        if (path instanceof StringPath sp) {
                            return sp.eq(filter.getValue());
                        }
                        if (path instanceof NumberPath<?> np) {
                            return buildNumberPredicate(np, filter.getValue(), NumberPath::eq);
                        }
                    } else if (FilterType.NOT_EQUALS.equals(filter.getFilterType())) {
                        if (path instanceof StringPath sp) {
                            return sp.ne(filter.getValue());
                        }
                        if (path instanceof NumberPath<?> np) {
                            return buildNumberPredicate(np, filter.getValue(), NumberPath::ne);
                        }
                    } else if (FilterType.LIKE.equals(filter.getFilterType())) {
                        if (path instanceof StringPath sp) {
                            return sp.like("%" + filter.getValue() + "%");
                        }
                    }

                    String err = String
                            .format("Unsupported Filter Type: `%s` for field: `%s`", filter.getFilterType(), filter.getField());
                    throw new IllegalArgumentException(err);
                })
                .reduce((p1, p2) -> CombineType.AND.equals(filterGroup.getCombineType()) ?
                        ((BooleanExpression) p1).and(p2) : ((BooleanExpression) p1).or(p2))
                .get();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number & Comparable<?>> Predicate buildNumberPredicate(
            NumberPath<?> path, String value, BiFunction<NumberPath<T>, T, Predicate> operator) {

        Class<T> type = (Class<T>) path.getType();
        if (type == Integer.class) {
            return operator.apply((NumberPath<T>) path, (T) Integer.valueOf(value));
        }
        if (type == Long.class) {
            return operator.apply((NumberPath<T>) path, (T) Long.valueOf(value));
        }
        if (type == Double.class) {
            return operator.apply((NumberPath<T>) path, (T) Double.valueOf(value));
        }
        if (type == BigDecimal.class) {
            return operator.apply((NumberPath<T>) path, (T) new BigDecimal(value));
        }

        throw new IllegalArgumentException("Unsupported number filterType: " + type);
    }
}
