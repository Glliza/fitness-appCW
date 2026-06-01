package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.UUID;

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

    // Добавляем поле для группировки в одну сессию
    @Column(name = "session_id")
    private String sessionId;
}