package my.utils.querydsl_utils.servise.other.field;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class FieldInfo {

    private final String label;
    private final FieldType type;
    private final ComparableExpressionBase<?> path;

}
