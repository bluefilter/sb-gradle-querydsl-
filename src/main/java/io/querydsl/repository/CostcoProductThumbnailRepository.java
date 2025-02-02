package io.querydsl.repository;

import io.querydsl.entity.CostcoProductThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CostcoProductThumbnailRepository extends JpaRepository<CostcoProductThumbnail, UUID> {
}
