package my.utils.querydsl_utils.example.repo;

import my.utils.querydsl_utils.example.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepo extends JpaRepository<TestEntity, Long> {
}
