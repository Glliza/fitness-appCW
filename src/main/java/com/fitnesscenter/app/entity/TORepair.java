package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "torepair")
public class TORepair extends BaseEntity {
    private String status;
    private String description;
    private String worker;
    private LocalDate plannedDate;
    private LocalDate completedDate;
    private String type;

    @Column(name = "equipment_id")  // ДОБАВЬТЕ ЭТО ПОЛЕ
    private Long equipmentId;
}