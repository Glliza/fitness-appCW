package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "torepair")
public class TORepair extends BaseEntity {

    private String status;

    @Size(max = 100, message = "Описание не должно превышать 100 символов")
    private String description;

    private String worker;

    @NotNull(message = "Плановая дата обязательна")
    @Future(message = "Дата ТО должна быть в будущем")
    private LocalDate plannedDate;

    private LocalDate completedDate;

    @NotBlank(message = "Тип ТО обязателен")
    private String type;

    private Long equipmentId;
}