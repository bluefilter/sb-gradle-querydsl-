package io.querydsl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "costco_members")
public class CostcoMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String email;

    // fetch = FetchType.LAZY (default), cascade의 기본값은 **CascadeType.NONE**입니다.
    @OneToMany(mappedBy = "costcoMember")
    private List<CostcoOrder> orders = new ArrayList<>();

    // 기본 생성자 (JPA에서 필요)
    protected CostcoMember() {
    }

    // 생성자 추가
    public CostcoMember(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

