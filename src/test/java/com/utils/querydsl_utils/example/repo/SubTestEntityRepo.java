package com.utils.querydsl_utils.example.repo;

import com.utils.querydsl_utils.example.entity.SubTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTestEntityRepo extends JpaRepository<SubTestEntity, Long> {
}
