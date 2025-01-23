package io.querydsl.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CostcoMemberUpdateRequestDTO {
    private UUID id;
    private String name;
    private String email;
}
