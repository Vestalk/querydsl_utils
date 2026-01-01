package my.utils.querydsl_utils.servise.other.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class FilterGroup {

    private final List<Filter> filters;
    private CombineType combineType = CombineType.AND;

    @Setter
    @Getter
    @RequiredArgsConstructor
    public static class Filter {
        private final String field;
        private final FilterType filterType;
        private final String value;
    }

}
