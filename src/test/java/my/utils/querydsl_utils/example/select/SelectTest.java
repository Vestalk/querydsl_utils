package my.utils.querydsl_utils.example.select;

import com.querydsl.jpa.impl.JPAQueryFactory;
import my.utils.querydsl_utils.example.dto.QTestEntityDto;
import my.utils.querydsl_utils.example.dto.TestEntityDto;
import my.utils.querydsl_utils.example.entity.QSubTestEntity;
import my.utils.querydsl_utils.example.entity.QTestEntity;
import my.utils.querydsl_utils.example.entity.SubTestEntity;
import my.utils.querydsl_utils.example.entity.TestEntity;
import my.utils.querydsl_utils.example.repo.SubTestEntityRepo;
import my.utils.querydsl_utils.example.repo.TestEntityRepo;
import my.utils.querydsl_utils.select.BaseSelect;
import my.utils.querydsl_utils.select.ProjectionSelect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SelectTest {

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private TestEntityRepo testEntityRepo;
    @Autowired
    private SubTestEntityRepo subTestEntityRepo;

    @BeforeAll
    public void initDB() {
        TestEntity te1 = new TestEntity();
        te1.setName("name1");
        SubTestEntity ste1 = new SubTestEntity();
        ste1.setName("subName1");
        ste1.setTestEntity(te1);
        te1.setSubTestEntities(List.of(ste1));
        testEntityRepo.save(te1);
        subTestEntityRepo.save(ste1);

        TestEntity te2 = new TestEntity();
        te2.setName("name2");
        SubTestEntity ste2 = new SubTestEntity();
        ste2.setName("subName2");
        ste2.setTestEntity(te2);
        SubTestEntity ste3 = new SubTestEntity();
        ste3.setName("subName3");
        ste3.setTestEntity(te2);
        te1.setSubTestEntities(List.of(ste2, ste3));
        testEntityRepo.save(te2);
        subTestEntityRepo.save(ste2);
        subTestEntityRepo.save(ste3);

        TestEntity te3 = new TestEntity();
        te3.setName("name3");
        testEntityRepo.save(te3);
    }

    @Test
    public void testBaseSelect() {
        QTestEntity te = QTestEntity.testEntity;
        BaseSelect<TestEntity> testEntityBaseSelect = BaseSelect.build(queryFactory, te);
        List<TestEntity> testEntities = testEntityBaseSelect.fetch();
        assertEquals(3, testEntities.size());

        QSubTestEntity ste = QSubTestEntity.subTestEntity;
        BaseSelect<SubTestEntity> subTestEntityBaseSelect = BaseSelect.build(queryFactory, ste);
        List<SubTestEntity> subTestEntities = subTestEntityBaseSelect.fetch();
        assertEquals(3, subTestEntities.size());
    }

    @Test
    public void testProjectionSelect() {
        QTestEntity te = QTestEntity.testEntity;
        QSubTestEntity ste = QSubTestEntity.subTestEntity;

        ProjectionSelect<TestEntity, TestEntityDto, QTestEntityDto> projectionSelect = ProjectionSelect
                .build(queryFactory, te, new QTestEntityDto(te.name, ste.name))
                .leftJoin(te.subTestEntities, ste);
        List<TestEntityDto> dtos = projectionSelect.fetch();
        assertEquals(4, dtos.size());

        Page<TestEntityDto> testEntityPage = projectionSelect.page(PageRequest.of(0, 10));
        assertEquals(4, testEntityPage.getTotalElements());
    }

}
