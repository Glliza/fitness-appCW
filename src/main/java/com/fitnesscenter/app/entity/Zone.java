package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "zone")
public class Zone extends BaseEntity {
    private Long userid;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private Integer capacity;

    private Integer floor;
}