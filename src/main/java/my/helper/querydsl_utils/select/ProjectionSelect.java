package my.helper.querydsl_utils.select;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class ProjectionSelect<E, D, QD extends Expression<D>>
        extends AbstractSelect<ProjectionSelect<E, D, QD>, E, D> {

    private ProjectionSelect(JPAQuery<D> jpaQuery,
                             EntityPathBase<E> entityPath) {
        super(jpaQuery, entityPath);
    }

    public static <E, D, QD extends Expression<D>> ProjectionSelect<E, D, QD> build(
            JPAQueryFactory queryFactory, EntityPathBase<E> entityPath,
            QD expr) {

        JPAQuery<D> jpaQuery = queryFactory.select(expr).from(entityPath);
        return new ProjectionSelect<E, D, QD>(jpaQuery, entityPath);
    }

}
