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
@Table(name = "costco_products_thumnail")
public class CostcoProductThumbnail {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Lob
    @Column(name = "thumbnail", nullable = false)
    private byte[] thumbnail;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    // 기본 생성자 (JPA에서 필요)
    protected CostcoProductThumbnail() {
    }

    public CostcoProductThumbnail(String name, byte[] thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }
}