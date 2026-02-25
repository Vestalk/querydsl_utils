package com.utils.querydsl_utils.servise.other.filter.strategy;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.utils.querydsl_utils.servise.other.filter.FilterGroup;
import com.utils.querydsl_utils.servise.other.filter.FilterType;

public interface FilterStrategy {

    Predicate getPredicate(Path<?> path, FilterGroup.Filter filter);

    FilterType getFilterType();

}
