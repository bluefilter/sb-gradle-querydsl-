package io.querydsl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "costco_products") // 테이블 이름 명시
public class CostcoProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // UUID는 DB에서 자동으로 생성
    private UUID id;

    private String name;

    private BigDecimal price;

    // 기본 생성자 (JPA에서 필요)
    protected CostcoProduct() {
    }

    // 생성자 추가
    public CostcoProduct(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
}


