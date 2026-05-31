package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "request_buy")
public class RequestBuy extends BaseEntity {

    private Long equipmentInventoryNumber;

    @NotBlank(message = "Наименование обязательно")
    @Size(min = 2, max = 50, message = "Наименование должно быть от 2 до 50 символов")
    private String name;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть больше 0")
    private Integer count;

    private String status;
}