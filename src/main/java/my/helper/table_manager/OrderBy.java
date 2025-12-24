package my.helper.table_manager;

import com.querydsl.core.types.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderBy {

    private final String field;
    private final Order order;

}
