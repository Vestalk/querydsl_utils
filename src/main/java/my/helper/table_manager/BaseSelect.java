package my.helper.table_manager;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaseSelect<T> {

    private final JPAQuery<T> jpaQuery;
    private final EntityPathBase<T> entityPath;

    protected BaseSelect(JPAQuery<T> jpaQuery, EntityPathBase<T> entityPath) {
        this.jpaQuery = jpaQuery;
        this.entityPath = entityPath;
    }

    public static <T> BaseSelect<T> build (JPAQueryFactory queryFactory, EntityPathBase<T> entityPath) {
        return new BaseSelect<T>(queryFactory.select(entityPath).from(entityPath), entityPath);
    }

    public BaseSelect<T> where (List<Predicate> predicates) {
        this.jpaQuery.where(predicates.toArray(Predicate[]::new));
        return this;
    }

    public BaseSelect<T> orderByFields (List<OrderBy> orders) {
        PathBuilder<T> pb = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata().getName());
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (OrderBy order: orders) {
            StringPath path = pb.getString(order.getField());
            OrderSpecifier<?> orderSpecifier =
                    Order.ASC.equals(order.getOrder())
                            ? path.asc()
                            : path.desc();
            orderSpecifiers.add(orderSpecifier);
        }
        return orderBySpecifier(orderSpecifiers);
    }

    public BaseSelect<T> orderBySpecifier (List<OrderSpecifier<?>> orderSpecifiers) {
        this.jpaQuery.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));
        return this;
    }

    public List<T> fetch () {
        return this.jpaQuery.fetch();
    }

    public Page<T> page (Pageable pageable) {
        Long total = this.jpaQuery.clone().select(entityPath.count()).fetchOne();
        List<T> content = this.jpaQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return new PageImpl<>(content, pageable, total);
    }
}
