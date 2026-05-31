package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "consumables_zone")
public class ConsumablesZone extends BaseEntity {

    @NotNull(message = "Расходный материал обязателен")
    private Long consumablesId;

    @NotNull(message = "Зона обязательна")
    private Long zoneId;

    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer count;
}