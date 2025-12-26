package my.helper.querydsl_utils.example.servise;

import my.helper.querydsl_utils.example.dto.TestEntityDto;
import my.helper.querydsl_utils.example.entity.SubTestEntity;
import my.helper.querydsl_utils.example.entity.TestEntity;
import my.helper.querydsl_utils.example.repo.SubTestEntityRepo;
import my.helper.querydsl_utils.example.repo.TestEntityRepo;
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
public class TestEntityProjectionSelectServiceTest {

    @Autowired
    private TestEntityRepo testEntityRepo;
    @Autowired
    private SubTestEntityRepo subTestEntityRepo;

    @Autowired
    private TestEntityProjectionSelectService testEntityProjectionSelectService;

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
    public void findAll_ByFilters_ByPredicate() {
        List<TestEntityDto> dtos1 = testEntityProjectionSelectService.findAllByFilters(List.of());
        assertEquals(4, dtos1.size());

        List<TestEntityDto> dtos2 = testEntityProjectionSelectService.findAllByPredicate(List.of());
        assertEquals(4, dtos2.size());
    }

    @Test
    public void getPage_ByFilters_ByPredicate() {
        Page<TestEntityDto> dtos1 = testEntityProjectionSelectService
                .getPageByFilters(List.of(), PageRequest.of(0, 10));
        assertEquals(4, dtos1.getTotalElements());

        Page<TestEntityDto> dtos2 = testEntityProjectionSelectService
                .getPageByPredicate(List.of(), PageRequest.of(0, 10));
        assertEquals(4, dtos2.getTotalElements());
    }
}
