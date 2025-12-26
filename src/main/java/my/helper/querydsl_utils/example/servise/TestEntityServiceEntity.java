package my.helper.querydsl_utils.example.servise;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.helper.querydsl_utils.example.entity.QTestEntity;
import my.helper.querydsl_utils.example.entity.TestEntity;
import my.helper.querydsl_utils.servise.AbstractEntitySelectService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestEntityServiceEntity extends AbstractEntitySelectService<TestEntity> {

    private static final QTestEntity entityPathBase = QTestEntity.testEntity;
    private static final Map<String, ComparableExpressionBase<?>> FIELD_MAP = Map.of(
            "id", entityPathBase.id,
            "name", entityPathBase.name
    );

    public TestEntityServiceEntity(JPAQueryFactory jpaQueryFactory) {
        super(entityPathBase, jpaQueryFactory);
    }


    @Override
    protected Map<String, ComparableExpressionBase<?>> getFieldMap() {
        return FIELD_MAP;
    }
}
