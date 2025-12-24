package my.helper.table_manager;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractSelect<T extends AbstractSelect<T, E, D>, E, D> {

    protected final JPAQuery<D> jpaQuery;
    protected final EntityPathBase<E> entityPath;

    public T where(List<Predicate> predicates) {
        jpaQuery.where(predicates.toArray(Predicate[]::new));
        return (T) this;
    }

    public T orderByFields(List<OrderBy> orders) {
        PathBuilder<E> pb = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata().getName());
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (OrderBy order : orders) {
            StringPath path = pb.getString(order.getField());
            OrderSpecifier<?> orderSpecifier =
                    Order.ASC.equals(order.getOrder())
                            ? path.asc()
                            : path.desc();
            orderSpecifiers.add(orderSpecifier);
        }
        return orderBySpecifier(orderSpecifiers);
    }

    public T orderBySpecifier(List<OrderSpecifier<?>> orderSpecifiers) {
        jpaQuery.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));
        return (T) this;
    }

    public List<D> fetch() {
        return this.jpaQuery.fetch();
    }

    public Page<D> page(Pageable pageable) {
        Long total = this.jpaQuery.clone().select(entityPath.count()).fetchOne();
        List<D> content = this.jpaQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return new PageImpl<>(content, pageable, total);
    }

}
