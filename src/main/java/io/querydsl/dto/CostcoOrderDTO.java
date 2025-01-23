package io.querydsl.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CostcoOrderDTO {
    private UUID id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;

    public CostcoOrderDTO(UUID id, LocalDateTime orderDate, BigDecimal totalAmount) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

}
