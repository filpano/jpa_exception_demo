package com.example.pg_group_column.demo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@IdClass(DemoEntityKey.class)
@Table(name = "entity_revenue", indexes = {@Index(columnList = "entityId, ts")})
public class DemoEntity {

    public DemoEntity() {
    }

    public DemoEntity(UUID entityId, Instant ts, BigDecimal revenue) {
        this.entityId = entityId;
        this.ts = ts;
        this.revenue = revenue;
    }

    @Id
    private UUID entityId;
    @Id
    private Instant ts;
    @Column(precision = 18, scale = 3)
    private BigDecimal revenue;

    @Override
    public String toString() {
        return "DemoEntity{" +
                "entityId=" + entityId +
                ", timestamp=" + ts +
                ", revenue=" + revenue +
                '}';
    }
}
