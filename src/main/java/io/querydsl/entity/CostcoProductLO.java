package io.querydsl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "costco_products_lo")
public class CostcoProductLO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "video_oid", nullable = false)
    private Long videoOid; // Large Object의 OID (Object Identifier)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    public CostcoProductLO() {
    }

    public CostcoProductLO(String name, Long videoOid) {
        this.name = name;
        this.videoOid = videoOid;
    }
}
