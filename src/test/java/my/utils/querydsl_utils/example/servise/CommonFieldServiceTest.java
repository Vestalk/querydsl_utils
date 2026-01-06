package my.utils.querydsl_utils.example.servise;

import my.utils.querydsl_utils.example.entity.SubTestEntity;
import my.utils.querydsl_utils.example.entity.TestEntity;
import my.utils.querydsl_utils.example.repo.SubTestEntityRepo;
import my.utils.querydsl_utils.example.repo.TestEntityRepo;
import my.utils.querydsl_utils.servise.CommonFieldService;
import my.utils.querydsl_utils.servise.other.field.FieldInfoDto;
import my.utils.querydsl_utils.servise.other.filter.CombineType;
import my.utils.querydsl_utils.servise.other.filter.FilterGroup;
import my.utils.querydsl_utils.servise.other.filter.FilterType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CommonFieldServiceTest {

    @Autowired
    private TestEntityRepo testEntityRepo;
    @Autowired
    private SubTestEntityRepo subTestEntityRepo;

    @Autowired
    private CommonFieldService commonFieldService;

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
    public void getFieldInfoDto__TestEntity() {
        List<FieldInfoDto> testEntityFieldInfos = commonFieldService.getFieldInfoDto("test-entity");
        assertEquals(2, testEntityFieldInfos.size());
    }

    @Test
    public void getFieldInfoDto__TestEntityDto() {
        List<FieldInfoDto> subTestEntityFieldInfos = commonFieldService.getFieldInfoDto("test-entity-dto");
        assertEquals(3, subTestEntityFieldInfos.size());
    }

    @Test
    public void getFieldInfoDto__Unsupported_MasterType() {
        assertThrows(RuntimeException.class, () ->
                commonFieldService.getFieldInfoDto("Unsupported_MasterType"));
    }

    @Test
    public void findDistinctFieldValues__TestEntity() {
        FilterGroup filterGroup = new FilterGroup(List.of(
                new FilterGroup.Filter("name", FilterType.EQUALS, "name1"),
                new FilterGroup.Filter("name", FilterType.EQUALS, "name2")
        ), CombineType.OR);

        List<?> values = commonFieldService
                .findDistinctFieldValues("test-entity", "name", List.of(filterGroup));

        assertEquals(2, values.size());
    }

    @Test
    public void findDistinctFieldValues__TestEntityDto() {
        FilterGroup filterGroup = new FilterGroup(List.of(
                new FilterGroup.Filter("subName", FilterType.EQUALS, "subName1"),
                new FilterGroup.Filter("subName", FilterType.EQUALS, "subName3")
        ), CombineType.OR);

        List<?> values = commonFieldService
                .findDistinctFieldValues("test-entity-dto", "subName", List.of(filterGroup));

        assertEquals(2, values.size());
    }

}