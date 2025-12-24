package my.helper.table_manager.entity;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class TEDto {

    private String name;

    @QueryProjection
    public TEDto(String name) {
        this.name = name;
    }
}
