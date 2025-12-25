package my.helper.table_manager.example.servise;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.helper.table_manager.example.dto.QTestEntityDto;
import my.helper.table_manager.example.dto.TestEntityDto;
import my.helper.table_manager.example.entity.QSubTestEntity;
import my.helper.table_manager.example.entity.QTestEntity;
import my.helper.table_manager.example.entity.TestEntity;
import my.helper.table_manager.servise.AbstractProjectionSelectService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestEntityProjectionSelectService extends AbstractProjectionSelectService<TestEntity, TestEntityDto, QTestEntity> {

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
    protected JPAQuery<TestEntityDto> modifyQuery(JPAQuery<TestEntityDto> query) {
        return query.join(testEntity.subTestEntities, subTestEntity);
    }

    @Override
    protected Map<String, ComparableExpressionBase<?>> getFieldMap() {
        return FIELD_MAP;
    }
}
