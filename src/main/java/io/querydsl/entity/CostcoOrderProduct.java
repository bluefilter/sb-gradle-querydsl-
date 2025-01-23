package io.querydsl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// OrderProduct 클래스
@Getter
@Setter
@Table(name = "costco_order_product") // 테이블 이름 명시
@Entity
public class CostcoOrderProduct {
    @EmbeddedId
    private CostcoOrderProductId id;

    private int quantity;

    @ManyToOne
    @MapsId("orderId")
    private CostcoOrder costcoOrders;

    @ManyToOne
    @MapsId("productId")
    private CostcoProduct product;
}

