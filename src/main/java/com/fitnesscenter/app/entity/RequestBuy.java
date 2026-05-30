package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "request_buy")
public class RequestBuy extends BaseEntity {
    private Long equipmentInventoryNumber;
    @Column(name = "name", length = 255)
    private String name;
    private Integer count;
    private String status;
}