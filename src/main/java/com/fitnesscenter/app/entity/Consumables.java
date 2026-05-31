package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "consumables")
public class Consumables extends BaseEntity {

    @NotBlank(message = "Название расходного материала обязательно")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    @Column(nullable = false, length = 50)
    private String name;

    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer realCount;
}