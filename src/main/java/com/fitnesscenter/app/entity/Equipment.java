package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "equipment")
public class Equipment extends BaseEntity {

    @Column(name = "zone_id")
    private Long zoneId;

    private String name;
    private String status;

    @Column(name = "data_buy")
    private LocalDate dataBuy;
}