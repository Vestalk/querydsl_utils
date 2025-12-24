package my.helper.table_manager;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import my.helper.table_manager.entity.TestEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class SelectHelper<T> {

    private final JPAQueryFactory queryFactory;
    private final EntityPath<T> entityPath;

    public List<T> findAll (List<Predicate> predicates) {
        return buildSelect()
                .where(predicates.toArray(Predicate[]::new))
                .fetch();
    }

    private JPAQuery<T> buildSelect () {
        return queryFactory
                .select(entityPath)
                .from(entityPath);
    }

    protected JPAQuery<Tuple> buildTupleSelect (List<String> fields) {
        return queryFactory
                .select(buildSelectExpressions(fields).toArray(Expression[]::new))
                .from(entityPath);
    }

    private List<? extends Expression<?>> buildSelectExpressions(List<String> fields ) {
        Class<? extends T> entityClass = entityPath.getType();
        String entityName = entityPath.getMetadata().getName();
        PathBuilder<T> pathBuilder = new PathBuilder<>(entityClass, entityName);
        return fields.stream()
                .map(pathBuilder::get)
                .filter(Objects::nonNull)
                .toList();
    }

}
