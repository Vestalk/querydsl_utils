package my.helper.querydsl_utils.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test")
public class TestEntity {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", columnDefinition = "varchar(255)")
    private String name;
    @OneToMany(mappedBy = "testEntity")
    private List<SubTestEntity> subTestEntities;

}
