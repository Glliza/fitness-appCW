package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "consumables_zone")
public class ConsumablesZone extends BaseEntity {

    private Long consumablesId;

    private Long zoneId;

    private Integer count;
}