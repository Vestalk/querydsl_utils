package my.helper.table_manager.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sub_test_entity")
public class SubTestEntity {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", columnDefinition = "varchar(255)")
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_entity_id")
    private TestEntity testEntity;

}
