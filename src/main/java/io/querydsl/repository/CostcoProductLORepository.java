package io.querydsl.repository;

import io.querydsl.entity.CostcoProductLO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CostcoProductLORepository extends JpaRepository<CostcoProductLO, UUID> {
    // 기본적인 CRUD 메소드들이 JpaRepository에 내장되어 있습니다.
}
