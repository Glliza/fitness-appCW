package com.fitnesscenter.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ZoneRq {
    private Long userid;

    @NotBlank(message = "Название зоны обязательно")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    private String name;

    @Size(max = 100, message = "Описание не должно превышать 100 символов")
    private String description;

    @NotNull(message = "Вместимость обязательна")
    @Positive(message = "Вместимость должна быть больше 0")
    private Integer capacity;

    @NotNull(message = "Этаж обязателен")
    @Min(value = 0, message = "Этаж не может быть отрицательным")
    private Integer floor;
}