package com.fitnesscenter.app.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "consumables")
public class Consumables extends BaseEntity {
    private String name;
    private Integer realCount;
}
