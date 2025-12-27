package my.utils.querydsl_utils.select;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class BaseSelect<E> extends AbstractSelect<BaseSelect<E>, E, E> {

    private BaseSelect(JPAQuery<E> jpaQuery, EntityPathBase<E> entityPath) {
        super(jpaQuery, entityPath);
    }

    public static <T> BaseSelect<T> build(JPAQueryFactory queryFactory, EntityPathBase<T> entityPath) {
        return new BaseSelect<T>(queryFactory.select(entityPath).from(entityPath), entityPath);
    }
}
