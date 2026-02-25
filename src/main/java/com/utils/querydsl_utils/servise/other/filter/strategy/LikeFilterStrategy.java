package com.utils.querydsl_utils.servise.other.filter.strategy;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringPath;
import com.utils.querydsl_utils.servise.other.filter.FilterGroup;
import com.utils.querydsl_utils.servise.other.filter.FilterType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeFilterStrategy implements FilterStrategy {

    @Override
    public Predicate getPredicate(Path<?> path, FilterGroup.Filter filter) {
        if (path instanceof StringPath sp) {
            return sp.like("%" + filter.getValue() + "%");
        }
        throw new IllegalArgumentException("Unsupported path type: " + path.getClass());
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.LIKE;
    }

}
