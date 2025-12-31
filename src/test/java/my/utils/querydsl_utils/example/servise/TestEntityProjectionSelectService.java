package my.utils.querydsl_utils.example.servise;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import my.utils.querydsl_utils.example.dto.QTestEntityDto;
import my.utils.querydsl_utils.example.dto.TestEntityDto;
import my.utils.querydsl_utils.example.entity.QSubTestEntity;
import my.utils.querydsl_utils.example.entity.QTestEntity;
import my.utils.querydsl_utils.example.entity.TestEntity;
import my.utils.querydsl_utils.servise.AbstractProjectionSelectService;
import my.utils.querydsl_utils.servise.other.FieldInfo;
import my.utils.querydsl_utils.servise.other.FieldType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestEntityProjectionSelectService extends AbstractProjectionSelectService<TestEntity, TestEntityDto> {

    private static final QTestEntity testEntity = QTestEntity.testEntity;
    private static final QSubTestEntity subTestEntity = QSubTestEntity.subTestEntity;
    private static final Map<String, FieldInfo> FIELD_MAP = Map.of(
            "id", new FieldInfo("ID", FieldType.NUMERIC, testEntity.id),
            "name", new FieldInfo("Name", FieldType.STRING, testEntity.name),
            "subName", new FieldInfo("Sub Name", FieldType.STRING, subTestEntity.name)
    );

    public TestEntityProjectionSelectService(JPAQueryFactory jpaQueryFactory) {
        super(FIELD_MAP, new QTestEntityDto(testEntity.name, subTestEntity.name), jpaQueryFactory, testEntity);
    }

    @Override
    protected <M> JPAQuery<M> modifyQuery(JPAQuery<M> query) {
        return query.leftJoin(testEntity.subTestEntities, subTestEntity);
    }
}
