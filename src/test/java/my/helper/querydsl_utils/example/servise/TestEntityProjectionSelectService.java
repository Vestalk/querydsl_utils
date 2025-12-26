package my.helper.querydsl_utils.example.servise;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.helper.querydsl_utils.example.dto.QTestEntityDto;
import my.helper.querydsl_utils.example.dto.TestEntityDto;
import my.helper.querydsl_utils.example.entity.QSubTestEntity;
import my.helper.querydsl_utils.example.entity.QTestEntity;
import my.helper.querydsl_utils.example.entity.TestEntity;
import my.helper.querydsl_utils.servise.AbstractProjectionSelectService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestEntityProjectionSelectService extends AbstractProjectionSelectService<TestEntity, TestEntityDto> {

    private static final QTestEntity testEntity = QTestEntity.testEntity;
    private static final QSubTestEntity subTestEntity = QSubTestEntity.subTestEntity;
    private static final Map<String, ComparableExpressionBase<?>> FIELD_MAP = Map.of(
            "id", testEntity.id,
            "name", testEntity.name,
            "subName", subTestEntity.name
    );

    public TestEntityProjectionSelectService(JPAQueryFactory jpaQueryFactory) {
        super(new QTestEntityDto(testEntity.name, subTestEntity.name), jpaQueryFactory, testEntity);
    }

    @Override
    protected <M> JPAQuery<M> modifyQuery(JPAQuery<M> query) {
        return query.leftJoin(testEntity.subTestEntities, subTestEntity);
    }

    @Override
    protected Map<String, ComparableExpressionBase<?>> getFieldMap() {
        return FIELD_MAP;
    }
}
