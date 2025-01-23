package io.querydsl.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CostcoMemberDTO {
    private UUID id;
    private String name;
    private String email;
    private List<CostcoOrderDTO> costcoOrders; // CostcoOrders를 포함하는 필드

    public CostcoMemberDTO(UUID id, String name, String email, List<CostcoOrderDTO> costcoOrders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.costcoOrders = costcoOrders;
    }
}

