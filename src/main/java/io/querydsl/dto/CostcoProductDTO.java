package io.querydsl.dto;

import io.querydsl.entity.CostcoProduct;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CostcoProductDTO {
    private UUID id;
    private String name;
    private BigDecimal price;

    public CostcoProductDTO(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // 정적 팩토리 메서드
    public static CostcoProductDTO fromEntity(CostcoProduct product) {
        if (product == null) {
            return null; // 안전성을 위해 null 처리
        }
        return new CostcoProductDTO(product.getId(), product.getName(), product.getPrice());
    }
}

