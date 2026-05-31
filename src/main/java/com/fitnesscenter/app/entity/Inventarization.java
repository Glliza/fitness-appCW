package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventarization")
public class Inventarization extends BaseEntity {
    private Long equipmentInventoryNumber;
    private Long zoneId;
    private Integer count;
    private Integer realCount;
    private LocalDate date;
}