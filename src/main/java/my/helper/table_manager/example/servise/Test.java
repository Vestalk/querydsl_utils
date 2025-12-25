package my.helper.table_manager.example.servise;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.List;

//@Component
@RequiredArgsConstructor
public class Test {

    private final TestEntityServiceEntity testEntityService;
    private final TestEntityProjectionSelectService testEntityProjectionSelectService;

    @PostConstruct
    public void test() {
//        List<Filter> filters = List.of(new Filter("name", FilterType.LIKE, "name1"));
//        var aa = testEntityService.findAllByFilters(filters);
//
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("name").descending());
//        var aa2 = testEntityService.getPageByFilters(filters, pageable);

        var bb = testEntityProjectionSelectService.findAllByPredicate(List.of());

        System.out.println("asd");
    }

}
