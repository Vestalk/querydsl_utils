package my.helper.table_manager;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import my.helper.table_manager.entity.QTestEntity;
import my.helper.table_manager.entity.TestEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestEntitySelectHelper extends SelectHelper<TestEntity> {

    public TestEntitySelectHelper(JPAQueryFactory queryFactory) {
        super(queryFactory, QTestEntity.testEntity);
    }

    public List<Tuple> selectTuple (List<String> fields) {
        JPAQuery<Tuple> query = buildTupleSelect(fields);

        return query.fetch();
    }

}
