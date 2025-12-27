package my.utils.querydsl_utils.select;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
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

    public T orderBySpecifier(List<OrderSpecifier<?>> orderSpecifiers) {
        jpaQuery.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));
        return (T) this;
    }

    public <J> T join(CollectionExpression<?, J> ep, Path<J> target) {
        jpaQuery.join(ep, target);
        return (T) this;
    }

    public <J> T leftJoin(CollectionExpression<?, J> ep, Path<J> target) {
        jpaQuery.leftJoin(ep, target);
        return (T) this;
    }

    public <J> T rightJoin(CollectionExpression<?, J> ep, Path<J> target) {
        jpaQuery.leftJoin(ep, target);
        return (T) this;
    }

    public T joinOn(List<Predicate> predicates) {
        jpaQuery.on(predicates.toArray(Predicate[]::new));
        return (T) this;
    }

    public T fetchJoin() {
        jpaQuery.fetchJoin();
        return (T) this;
    }

    public List<D> fetch() {
        return this.jpaQuery.fetch();
    }

    public Page<D> page(Pageable pageable) {
        Long total = this.jpaQuery.clone().select(entityPath.count()).fetchOne();
        List<D> content = new ArrayList<>();
        if (total != 0) {
            content = this.jpaQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        }
        return new PageImpl<>(content, pageable, total);
    }

}
