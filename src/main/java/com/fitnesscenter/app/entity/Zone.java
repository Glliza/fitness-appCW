package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "zone")
public class Zone extends BaseEntity {

    private Long userid;

    @NotBlank(message = "Название зоны обязательно")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Size(max = 100, message = "Описание не должно превышать 100 символов")
    @Column(length = 100)
    private String description;

    @NotNull(message = "Вместимость обязательна")
    @Positive(message = "Вместимость должна быть больше 0")
    private Integer capacity;

    @NotNull(message = "Этаж обязателен")
    @Min(value = 0, message = "Этаж не может быть отрицательным")
    private Integer floor;
}