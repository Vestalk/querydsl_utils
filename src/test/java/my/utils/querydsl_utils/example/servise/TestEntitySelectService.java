package my.utils.querydsl_utils.example.servise;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.utils.querydsl_utils.example.entity.QTestEntity;
import my.utils.querydsl_utils.example.entity.TestEntity;
import my.utils.querydsl_utils.servise.AbstractEntitySelectService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestEntitySelectService extends AbstractEntitySelectService<TestEntity> {

    private static final QTestEntity entityPathBase = QTestEntity.testEntity;
    private static final Map<String, ComparableExpressionBase<?>> FIELD_MAP = Map.of(
            "id", entityPathBase.id,
            "name", entityPathBase.name
    );

    public TestEntitySelectService(JPAQueryFactory jpaQueryFactory) {
        super(FIELD_MAP, entityPathBase, jpaQueryFactory);
    }
}
