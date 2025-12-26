package my.helper.querydsl_utils.servise.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
//@AllArgsConstructor
@RequiredArgsConstructor
public class Filter {

    private final String field;
    private final FilterType filterType;
    private final String value;

    // TODO
//    private CombineType combineType = CombineType.AND;

}
