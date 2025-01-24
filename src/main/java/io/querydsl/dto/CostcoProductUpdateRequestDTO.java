package io.querydsl.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CostcoProductUpdateRequestDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
}
