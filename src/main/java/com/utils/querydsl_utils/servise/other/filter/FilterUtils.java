package com.utils.querydsl_utils.servise.other.filter;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberPath;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class FilterUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<?>> Predicate buildNumberPredicate(
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
