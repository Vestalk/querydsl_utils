package my.helper.table_manager.example.select;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import my.helper.table_manager.example.dto.QTestEntityDto;
import my.helper.table_manager.example.dto.TestEntityDto;
import my.helper.table_manager.example.entity.QSubTestEntity;
import my.helper.table_manager.example.entity.QTestEntity;
import my.helper.table_manager.example.entity.TestEntity;
import my.helper.table_manager.select.BaseSelect;
import my.helper.table_manager.select.ProjectionSelect;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
