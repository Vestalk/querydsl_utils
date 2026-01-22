package my.utils.querydsl_utils.servise.other.filter;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import my.utils.querydsl_utils.config.AppProperties;
import my.utils.querydsl_utils.servise.other.field.FieldInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class FilterToPredicateMapper {

    private final AppProperties appProperties;

    private DateTimeFormatter DATE_FORMATTER;
    private DateTimeFormatter TIME_FORMATTER;
    private DateTimeFormatter DATE_TIME_FORMATTER;

    @PostConstruct
    public void init() {
        AppProperties.Patterns patterns = appProperties.getPatterns();
        DATE_FORMATTER = DateTimeFormatter.ofPattern(patterns.getDatePattern());
        TIME_FORMATTER = DateTimeFormatter.ofPattern(patterns.getTimePattern());
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(patterns.getDateTimePattern());
    }

    public List<Predicate> getPredicates(Map<String, FieldInfo> fieldMap,
                                                List<FilterGroup> filterGroups) {

        return filterGroups.stream()
                .map(group -> buildPredicates(fieldMap, group))
                .toList();
    }

    private Predicate buildPredicates(Map<String, FieldInfo> fieldMap,
                                             FilterGroup filterGroup) {

        return filterGroup.getFilters()
                .stream()
                .filter(filter -> fieldMap.containsKey(filter.getField()))
                .map(filter -> {
                    Path<?> path = (Path<?>) fieldMap.get(filter.getField()).getPath();

                    if (FilterType.EQUALS.equals(filter.getFilterType())) {
                        if (path instanceof StringPath sp) {
                            return sp.eq(filter.getValue());
                        }
                        if (path instanceof NumberPath<?> np) {
                            return buildNumberPredicate(np, filter.getValue(), NumberPath::eq);
                        }
                        if (path instanceof DatePath<?> dp) {
                            LocalDate value = LocalDate.parse(filter.getValue(), DATE_FORMATTER);
                            return ((DatePath<LocalDate>) dp).eq(value);
                        }
                        if (path instanceof TimePath<?> tp) {
                            LocalTime value = LocalTime.parse(filter.getValue(), TIME_FORMATTER);
                            return ((TimePath<LocalTime>) tp).eq(value);
                        }
                        if (path instanceof DateTimePath<?> dtp) {
                            LocalDateTime value = LocalDateTime.parse(filter.getValue(), DATE_TIME_FORMATTER);
                            return ((DateTimePath<LocalDateTime>) dtp).eq(value);
                        }
                    } else if (FilterType.NOT_EQUALS.equals(filter.getFilterType())) {
                        if (path instanceof StringPath sp) {
                            return sp.ne(filter.getValue());
                        }
                        if (path instanceof NumberPath<?> np) {
                            return buildNumberPredicate(np, filter.getValue(), NumberPath::ne);
                        }
                        if (path instanceof DatePath<?> dp) {
                            LocalDate value = LocalDate.parse(filter.getValue(), DATE_FORMATTER);
                            return ((DatePath<LocalDate>) dp).ne(value);
                        }
                        if (path instanceof TimePath<?> tp) {
                            LocalTime value = LocalTime.parse(filter.getValue(), TIME_FORMATTER);
                            return ((TimePath<LocalTime>) tp).ne(value);
                        }
                        if (path instanceof DateTimePath<?> dtp) {
                            LocalDateTime value = LocalDateTime.parse(filter.getValue(), DATE_TIME_FORMATTER);
                            return ((DateTimePath<LocalDateTime>) dtp).ne(value);
                        }
                    } else if (FilterType.LIKE.equals(filter.getFilterType())) {
                        if (path instanceof StringPath sp) {
                            return sp.like("%" + filter.getValue() + "%");
                        }
                    } else if (FilterType.BEFORE.equals(filter.getFilterType())) {
                        if (path instanceof DatePath<?> dp) {
                            LocalDate value = LocalDate.parse(filter.getValue(), DATE_FORMATTER);
                            return ((DatePath<LocalDate>) dp).before(value);
                        }
                        if (path instanceof TimePath<?> tp) {
                            LocalTime value = LocalTime.parse(filter.getValue(), TIME_FORMATTER);
                            return ((TimePath<LocalTime>) tp).before(value);
                        }
                        if (path instanceof DateTimePath<?> dtp) {
                            LocalDateTime value = LocalDateTime.parse(filter.getValue(), DATE_TIME_FORMATTER);
                            return ((DateTimePath<LocalDateTime>) dtp).before(value);
                        }
                    } else if (FilterType.AFTER.equals(filter.getFilterType())) {
                        if (path instanceof DatePath<?> dp) {
                            LocalDate value = LocalDate.parse(filter.getValue(), DATE_FORMATTER);
                            return ((DatePath<LocalDate>) dp).after(value);
                        }
                        if (path instanceof TimePath<?> dtp) {
                            LocalTime value = LocalTime.parse(filter.getValue(), TIME_FORMATTER);
                            return ((TimePath<LocalTime>) dtp).after(value);
                        }
                        if (path instanceof DateTimePath<?> dtp) {
                            LocalDateTime value = LocalDateTime.parse(filter.getValue(), DATE_TIME_FORMATTER);
                            return ((DateTimePath<LocalDateTime>) dtp).after(value);
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
