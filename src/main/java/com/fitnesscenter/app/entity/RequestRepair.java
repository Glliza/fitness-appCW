package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "request_repair")
public class RequestRepair extends BaseEntity {

    private Long TORepairId;

    @NotNull(message = "Оборудование обязательно")
    private Long equipmentInventoryNumber;

    private String status;

    private String worker;

    @Size(max = 100, message = "Описание не должно превышать 100 символов")
    private String description;

    @NotBlank(message = "Укажите, кто зафиксировал поломку")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String creator;
}