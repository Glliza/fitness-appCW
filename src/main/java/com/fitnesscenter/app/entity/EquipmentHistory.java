package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "equipment_history")
public class EquipmentHistory extends BaseEntity {

    private String equipmentHistoryNumber;

    private String type;

    private LocalDate date;

    private String worker;

    private String description;
}