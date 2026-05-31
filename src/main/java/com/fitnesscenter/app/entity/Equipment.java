package com.fitnesscenter.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "equipment")
public class Equipment extends BaseEntity {

    @NotNull(message = "Зона обязательна")
    @Column(name = "zone_id")
    private Long zoneId;

    @NotBlank(message = "Название оборудования обязательно")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    @Column(nullable = false, length = 50)
    private String name;

    private String status;

    @NotNull(message = "Дата покупки обязательна")
    @Past(message = "Дата покупки должна быть в прошлом")
    @Column(name = "data_buy")
    private LocalDate dataBuy;
}