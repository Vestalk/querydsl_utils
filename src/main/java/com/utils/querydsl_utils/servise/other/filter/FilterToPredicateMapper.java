package com.utils.querydsl_utils.servise.other.filter;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.utils.querydsl_utils.servise.other.field.FieldInfo;
import com.utils.querydsl_utils.servise.other.filter.strategy.FilterStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FilterToPredicateMapper {

    private final Map<FilterType, FilterStrategy> strategies;

    public FilterToPredicateMapper(List<FilterStrategy> strategies) {
        this.strategies = strategies
                .stream()
                .collect(Collectors.toMap(FilterStrategy::getFilterType, Function.identity()));
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
                    if (strategies.containsKey(filter.getFilterType())) {
                        Path<?> path = (Path<?>) fieldMap.get(filter.getField()).getPath();
                        return strategies.get(filter.getFilterType()).getPredicate(path, filter);
                    }
                    String err = String
                            .format("Unsupported Filter Type: `%s` for field: `%s`", filter.getFilterType(), filter.getField());
                    throw new IllegalArgumentException(err);
                })
                .reduce((p1, p2) -> CombineType.AND.equals(filterGroup.getCombineType()) ?
                        ((BooleanExpression) p1).and(p2) : ((BooleanExpression) p1).or(p2))
                .get();
    }
}
