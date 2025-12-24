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

public class BaseSelect<E> extends AbstractSelect<BaseSelect<E>, E>{

//    private final EntityPathBase<E> entityPath;

    protected BaseSelect(JPAQuery<E> jpaQuery, EntityPathBase<E> entityPath) {
        super(jpaQuery, entityPath);
    }

    public static <T> BaseSelect<T> build (JPAQueryFactory queryFactory, EntityPathBase<T> entityPath) {
        return new BaseSelect<T>(queryFactory.select(entityPath).from(entityPath), entityPath);
    }

//    public BaseSelect<E> where (List<Predicate> predicates) {
//        this.jpaQuery.where(predicates.toArray(Predicate[]::new));
//        return this;
//    }
//
//    public BaseSelect<E> orderByFields (List<OrderBy> orders) {
//        PathBuilder<E> pb = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata().getName());
//        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
//        for (OrderBy order: orders) {
//            StringPath path = pb.getString(order.getField());
//            OrderSpecifier<?> orderSpecifier =
//                    Order.ASC.equals(order.getOrder())
//                            ? path.asc()
//                            : path.desc();
//            orderSpecifiers.add(orderSpecifier);
//        }
//        return orderBySpecifier(orderSpecifiers);
//    }

//    public BaseSelect<E> orderBySpecifier (List<OrderSpecifier<?>> orderSpecifiers) {
//        this.jpaQuery.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));
//        return this;
//    }

//    public List<E> fetch () {
//        return this.jpaQuery.fetch();
//    }
//
//    public Page<E> page (Pageable pageable) {
//        Long total = this.jpaQuery.clone().select(entityPath.count()).fetchOne();
//        List<E> content = this.jpaQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
//        return new PageImpl<>(content, pageable, total);
//    }
}
