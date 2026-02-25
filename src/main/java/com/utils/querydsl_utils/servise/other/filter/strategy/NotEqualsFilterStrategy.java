package com.utils.querydsl_utils.servise.other.filter.strategy;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.utils.querydsl_utils.config.AppProperties;
import com.utils.querydsl_utils.servise.other.filter.FilterGroup;
import com.utils.querydsl_utils.servise.other.filter.FilterType;
import com.utils.querydsl_utils.servise.other.filter.FilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class NotEqualsFilterStrategy implements FilterStrategy {

    private final AppProperties appProperties;

    @Override
    public Predicate getPredicate(Path<?> path, FilterGroup.Filter filter) {
        if (path instanceof StringPath sp) {
            return sp.ne(filter.getValue());
        } else if (path instanceof NumberPath<?> np) {
            return FilterUtils.buildNumberPredicate(np, filter.getValue(), NumberPath::ne);
        } else if (path instanceof DatePath<?> dp) {
            LocalDate value = LocalDate.parse(filter.getValue(), appProperties.getFormat().getDateFormatter());
            return ((DatePath<LocalDate>) dp).ne(value);
        } else if (path instanceof TimePath<?> tp) {
            LocalTime value = LocalTime.parse(filter.getValue(), appProperties.getFormat().getTimeFormatter());
            return ((TimePath<LocalTime>) tp).ne(value);
        } else if (path instanceof DateTimePath<?> dtp) {
            LocalDateTime value = LocalDateTime.parse(filter.getValue(), appProperties.getFormat().getDateTimeFormatter());
            return ((DateTimePath<LocalDateTime>) dtp).ne(value);
        }
        throw new IllegalArgumentException("Unsupported path type: " + path.getClass());
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.NOT_EQUALS;
    }
}
