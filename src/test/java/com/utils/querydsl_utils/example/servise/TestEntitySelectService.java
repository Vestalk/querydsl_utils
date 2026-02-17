package com.utils.querydsl_utils.example.servise;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.utils.querydsl_utils.example.entity.QTestEntity;
import com.utils.querydsl_utils.example.entity.TestEntity;
import com.utils.querydsl_utils.servise.AbstractEntitySelectService;
import com.utils.querydsl_utils.servise.other.field.FieldInfo;
import com.utils.querydsl_utils.servise.other.field.FieldType;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.utils.querydsl_utils.example.entity.TestEntity.Fields.*;

@Component
public class TestEntitySelectService extends AbstractEntitySelectService<TestEntity> {

    private static final QTestEntity entityPathBase = QTestEntity.testEntity;
    private static final Map<String, FieldInfo> FIELD_MAP = Map.of(
            id, new FieldInfo("ID", FieldType.NUMERIC, entityPathBase.id),
            name, new FieldInfo("Name", FieldType.STRING, entityPathBase.name),
            time, new FieldInfo("Time", FieldType.TIME, entityPathBase.time),
            date, new FieldInfo("Date", FieldType.DATE, entityPathBase.date),
            dateTime, new FieldInfo("Date/Time", FieldType.DATE_TIME, entityPathBase.dateTime)
    );

    public TestEntitySelectService(JPAQueryFactory jpaQueryFactory) {
        super(FIELD_MAP, entityPathBase, jpaQueryFactory);
    }

    @Override
    public String getMasterType() {
        return "test-entity";
    }
}
