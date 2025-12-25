package my.helper.table_manager.example.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class TestEntityDto {

    private String name;
    private String subName;

    @QueryProjection
    public TestEntityDto(String name, String subName) {
        this.name = name;
        this.subName = subName;
    }
}
