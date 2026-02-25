package com.utils.querydsl_utils.servise.other.filter.strategy;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.TimePath;
import com.utils.querydsl_utils.config.AppProperties;
import com.utils.querydsl_utils.servise.other.filter.FilterGroup;
import com.utils.querydsl_utils.servise.other.filter.FilterType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class AfterFilterTypeStrategy implements FilterStrategy {

    private final AppProperties appProperties;

    @Override
    public Predicate getPredicate(Path<?> path, FilterGroup.Filter filter) {
        if (path instanceof DatePath<?> dp) {
            LocalDate value = LocalDate.parse(filter.getValue(), appProperties.getFormat().getDateFormatter());
            return ((DatePath<LocalDate>) dp).after(value);
        }
        if (path instanceof TimePath<?> dtp) {
            LocalTime value = LocalTime.parse(filter.getValue(), appProperties.getFormat().getTimeFormatter());
            return ((TimePath<LocalTime>) dtp).after(value);
        }
        if (path instanceof DateTimePath<?> dtp) {
            LocalDateTime value = LocalDateTime.parse(filter.getValue(), appProperties.getFormat().getDateTimeFormatter());
            return ((DateTimePath<LocalDateTime>) dtp).after(value);
        }
        throw new IllegalArgumentException("Unsupported path type: " + path.getClass());
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.AFTER;
    }

}
