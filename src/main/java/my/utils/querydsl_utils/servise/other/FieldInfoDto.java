package my.utils.querydsl_utils.servise.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FieldInfoDto {
    private String name;
    private String label;
    private FieldType type;
}
