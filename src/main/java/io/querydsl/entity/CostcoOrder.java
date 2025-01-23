package io.querydsl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "costco_orders") // 테이블 이름 명시
public class CostcoOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // UUID는 DB에서 자동으로 생성
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private CostcoMember costcoMember;

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;
}
