package my.helper.table_manager;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import my.helper.table_manager.entity.QTEDto;
import my.helper.table_manager.entity.QTestEntity;
import my.helper.table_manager.entity.TEDto;
import my.helper.table_manager.entity.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class Test {

    private final JPAQueryFactory queryFactory;

    private final TestEntitySelectHelper selectHelper;

    @PostConstruct
    public void ii () {
        QTestEntity te = QTestEntity.testEntity;

        te.id.asc();

        List<TestEntity> a = queryFactory.selectFrom(te).from(te).where()
                .orderBy(te.id.asc(), te.name.asc()).fetch();

        new QTEDto(te.name);
        JPAQuery<TEDto> aaa = queryFactory.select(new QTEDto(te.name)).from(te);
        List<TEDto> a2 = aaa.fetch();

        ProjectionSelect<TestEntity, TEDto, QTEDto> dtoProjectionSelect =
                ProjectionSelect
                        .build(queryFactory, te, new QTEDto(te.name))
                        .where(List.of(te.name.like("%me2%")))
                        .orderByFields(List.of(new OrderBy("name", Order.ASC), new OrderBy("id", Order.DESC)));
        var rr = dtoProjectionSelect.fetch();
        System.out.println("asd");

//        List<String> fields = List.of("name");
//        Predicate predicate = te.name.eq("name1");
//        List<Tuple> a3 = find(te, List.of(predicate), fields);
//        for (Tuple tuple : a3) {
//            String firstName = tuple.get(te.name);
//            Long lastName = tuple.get(te.id);
//            System.out.println(firstName + " " + lastName);
//        }

//        List<String> fields = List.of("name");
//        var  a3 = selectHelper.findAll(List.of());
//        for (Tuple tuple : a3) {
//            String firstName = tuple.get(te.name);
//            Long lastName = tuple.get(te.id);
//            System.out.println(firstName + " " + lastName);
//        }

        BaseSelect<TestEntity> baseSelect = BaseSelect
                .build(queryFactory, te)
                .where(List.of(te.name.like("%me2%")))
                .orderByFields(List.of(new OrderBy("name", Order.ASC), new OrderBy("id", Order.DESC)));
//        List<TestEntity> testEntities = baseSelect.fetch();

        Page<TestEntity> listaaa = baseSelect.page(PageRequest.of(0, 1));

        System.out.println("asd");

    }

    public <T> List<Tuple> find2 (EntityPath<T> entityPath, List<Predicate> predicates, List<String> fields) {
        return queryFactory
                .select(buildSelectExpressions(entityPath, fields).toArray(Expression[]::new))
                .from(entityPath)
                .where(predicates.toArray(Predicate[]::new))
                .fetch();
    }

    public <T> List<Tuple> find (EntityPath<T> entityPath, List<Predicate> predicates, List<String> fields) {
        return queryFactory
                .select(buildSelectExpressions(entityPath, fields).toArray(Expression[]::new))
                .from(entityPath)
                .where(predicates.toArray(Predicate[]::new))
                .fetch();
    }

    public <T> List<? extends Expression<?>> buildSelectExpressions(EntityPath<T> entityPath, List<String> fields ) {
        Class<? extends T> entityClass = entityPath.getType();
        String entityName = entityPath.getMetadata().getName();
        PathBuilder<T> pathBuilder = new PathBuilder<>(entityClass, entityName);
        return fields.stream()
                .map(pathBuilder::get)
                .filter(Objects::nonNull)
                .toList();
    }


}
