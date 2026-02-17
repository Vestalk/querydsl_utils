package com.utils.querydsl_utils.example.servise;

import com.utils.querydsl_utils.example.entity.SubTestEntity;
import com.utils.querydsl_utils.example.entity.TestEntity;
import com.utils.querydsl_utils.example.repo.SubTestEntityRepo;
import com.utils.querydsl_utils.example.repo.TestEntityRepo;
import com.utils.querydsl_utils.servise.other.filter.CombineType;
import com.utils.querydsl_utils.servise.other.filter.FilterGroup;
import com.utils.querydsl_utils.servise.other.filter.FilterType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.utils.querydsl_utils.example.entity.TestEntity.Fields.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestEntitySelectServiceTest {

    @Autowired
    private TestEntityRepo testEntityRepo;
    @Autowired
    private SubTestEntityRepo subTestEntityRepo;

    @Autowired
    private TestEntitySelectService testEntityService;

    @BeforeAll
    public void initDB() {
        TestEntity te1 = new TestEntity();
        te1.setName("name1");
        te1.setTime(LocalTime.now());
        te1.setDate(LocalDate.now());
        te1.setDateTime(LocalDateTime.now());
        SubTestEntity ste1 = new SubTestEntity();
        ste1.setName("subName1");
        ste1.setTestEntity(te1);
        te1.setSubTestEntities(List.of(ste1));
        testEntityRepo.save(te1);
        subTestEntityRepo.save(ste1);

        TestEntity te2 = new TestEntity();
        te2.setName("name2");
        LocalTime lt = LocalTime.of(12, 30);
        LocalDate ld = LocalDate.of(2026, 1, 1);
        te2.setTime(lt);
        te2.setDate(ld);
        te2.setDateTime(LocalDateTime.of(ld, lt));
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
        te3.setTime(LocalTime.now());
        te3.setDate(LocalDate.now());
        te3.setDateTime(LocalDateTime.now());
        testEntityRepo.save(te3);
    }

    @Test
    public void findDistinctFieldValues() {
        FilterGroup filterGroup = new FilterGroup(List.of(
                new FilterGroup.Filter(name, FilterType.EQUALS, "name1"),
                new FilterGroup.Filter(name, FilterType.EQUALS, "name2")
        ), CombineType.OR);

        List<?> names = testEntityService.findDistinctFieldValuesByFilterGroups(name, List.of(filterGroup));

        assertEquals(2, names.size());
    }

    @Test
    public void findAll_ByFilters_ByPredicate() {

        FilterGroup filterGroup1 = new FilterGroup(List.of(
                new FilterGroup.Filter(name, FilterType.EQUALS, "name1"),
                new FilterGroup.Filter(name, FilterType.EQUALS, "name2")
        ), CombineType.OR);
        FilterGroup filterGroup2 = new FilterGroup(List.of(
                new FilterGroup.Filter("dateTime", FilterType.BEFORE, "01-01-2026 12:35:00")
        ));

        List<FilterGroup> filterGroups = List.of(filterGroup1, filterGroup2);
        List<TestEntity> testEntities1 = testEntityService.findAllByFilters(filterGroups);
        assertEquals(1, testEntities1.size());

        List<TestEntity> testEntities2 = testEntityService.findAllByPredicate(List.of());
        assertEquals(3, testEntities2.size());
    }

    @Test
    public void getPage_ByFilters_ByPredicate() {
        Page<TestEntity> testEntities1 = testEntityService
                .getPageByFilters(List.of(), PageRequest.of(0, 10, Sort.by(id).ascending()));
        assertEquals(3, testEntities1.getTotalElements());

        Page<TestEntity> testEntities2 = testEntityService
                .getPageByPredicate(List.of(), PageRequest.of(0, 10));
        assertEquals(3, testEntities2.getTotalElements());
    }

    @Test
    public void findAll_fields_param_ByFilters_ByPredicate() {
        FilterGroup filterGroup = new FilterGroup(List.of(
                new FilterGroup.Filter(name, FilterType.EQUALS, "name1"),
                new FilterGroup.Filter(name, FilterType.EQUALS, "name2")
        ), CombineType.OR);
        List<FilterGroup> filterGroups = List.of(filterGroup);
        List<Map<String, Object>> list1 = testEntityService.findAllByFilters(List.of(name), filterGroups);
        assertEquals(2, list1.size());

        List<Map<String, Object>> list2 = testEntityService.findAllByPredicate(List.of(name), List.of());
        assertEquals(3, list2.size());
    }

    @Test
    public void getPage_fields_param_ByFilters_ByPredicate() {
        Page<Map<String, Object>> testEntities1 = testEntityService
                .getPageByFilters(List.of(name), List.of(), PageRequest.of(0, 10, Sort.by(id).ascending()));
        assertEquals(3, testEntities1.getTotalElements());

        Page<Map<String, Object>> testEntities2 = testEntityService
                .getPageByPredicate(List.of(name), List.of(), PageRequest.of(0, 10));
        assertEquals(3, testEntities2.getTotalElements());
    }
}
