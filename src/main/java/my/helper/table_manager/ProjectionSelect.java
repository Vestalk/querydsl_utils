package my.helper.table_manager;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.helper.table_manager.entity.QTEDto;
import my.helper.table_manager.entity.TEDto;
import my.helper.table_manager.entity.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ProjectionSelect <E, D, QD extends Expression<D>> {

    private final JPAQuery<D> jpaQuery;
    private final EntityPathBase<E> entityPath;

    protected ProjectionSelect(JPAQuery<D> jpaQuery,
                               EntityPathBase<E> entityPath) {
        this.jpaQuery = jpaQuery;
        this.entityPath = entityPath;
    }

    public static <E, D, QD extends Expression<D>> ProjectionSelect<E, D, QD> build (JPAQueryFactory queryFactory, EntityPathBase<E> entityPath,
                                                                          QD expr) {

        JPAQuery<D> jpaQuery = queryFactory.select(expr).from(entityPath);
        return new ProjectionSelect<E, D, QD>(jpaQuery, entityPath);
    }

    public ProjectionSelect<E, D, QD> where (List<Predicate> predicates) {
        this.jpaQuery.where(predicates.toArray(Predicate[]::new));
        return this;
    }

    public ProjectionSelect<E, D, QD> orderByFields (List<OrderBy> orders) {
        PathBuilder<E> pb = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata().getName());
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

    public ProjectionSelect<E, D, QD> orderBySpecifier (List<OrderSpecifier<?>> orderSpecifiers) {
        this.jpaQuery.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));
        return this;
    }

    public List<D> fetch () {
        return this.jpaQuery.fetch();
    }

    public Page<D> page (Pageable pageable) {
        Long total = this.jpaQuery.clone().select(entityPath.count()).fetchOne();
        List<D> content = this.jpaQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return new PageImpl<>(content, pageable, total);
    }

}
