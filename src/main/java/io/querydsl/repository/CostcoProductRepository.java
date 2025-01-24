package io.querydsl.repository;

import io.querydsl.entity.CostcoProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CostcoProductRepository extends JpaRepository<CostcoProduct, UUID> {

}