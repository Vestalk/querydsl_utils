package my.helper.querydsl_utils.example.select;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import my.helper.querydsl_utils.example.dto.QTestEntityDto;
import my.helper.querydsl_utils.example.dto.TestEntityDto;
import my.helper.querydsl_utils.example.entity.QSubTestEntity;
import my.helper.querydsl_utils.example.entity.QTestEntity;
import my.helper.querydsl_utils.example.entity.TestEntity;
import my.helper.querydsl_utils.select.BaseSelect;
import my.helper.querydsl_utils.select.ProjectionSelect;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
@RequiredArgsConstructor
public class Test {

    private final JPAQueryFactory queryFactory;

    @PostConstruct
    public void test() {
        QTestEntity te = QTestEntity.testEntity;
        QSubTestEntity ste = QSubTestEntity.subTestEntity;

        BaseSelect<TestEntity> baseSelect = BaseSelect
                .build(queryFactory, te)
                .join(te.subTestEntities, ste)
                .where(List.of(ste.name.like("snt")));
        var aa = baseSelect.fetch();
//
        ProjectionSelect<TestEntity, TestEntityDto, QTestEntityDto> projectionSelect = ProjectionSelect
                .build(queryFactory, te, new QTestEntityDto(te.name, ste.name))
                .join(te.subTestEntities, ste);
        var bb = projectionSelect.fetch();

        System.out.println("asd");

    }

}
