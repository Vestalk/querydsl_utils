package my.helper.querydsl_utils.servise.other;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class FilterToPredicateMapper {

    public static List<Predicate> getPredicates(Map<String, ComparableExpressionBase<?>> fieldMap, List<Filter> filters) {
        return filters.stream()
                .filter(filter -> fieldMap.containsKey(filter.field()))
                .map(filter -> {
                    Path<?> path = (Path<?>) fieldMap.get(filter.field());

                    if (FilterType.EQUALS.equals(filter.type())) {
                        if (path instanceof StringPath sp) {
                            return sp.eq(filter.value());
                        }
                        if (path instanceof NumberPath<?> np) {
                            return buildNumberPredicate(np, filter.value(), NumberPath::eq);
                        }
                    } else if (FilterType.NOT_EQUALS.equals(filter.type())) {
                        if (path instanceof StringPath sp) {
                            return sp.ne(filter.value());
                        }
                        if (path instanceof NumberPath<?> np) {
                            return buildNumberPredicate(np, filter.value(), NumberPath::ne);
                        }
                    } else if (FilterType.LIKE.equals(filter.type())) {
                        if (path instanceof StringPath sp) {
                            return sp.like("%" + filter.value() + "%");
                        }
                    }

                    String err = String
                            .format("Unsupported Filter Type: `%s` for field: `%s`", filter.type(), filter.field());
                    throw new IllegalArgumentException(err);
                })
                .toList();
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

        throw new IllegalArgumentException("Unsupported number type: " + type);
    }
}
