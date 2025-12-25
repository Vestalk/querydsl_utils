package my.helper.table_manager.servise;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public abstract class AbstractSelectService {

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
