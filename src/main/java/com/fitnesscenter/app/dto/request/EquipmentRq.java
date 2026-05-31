package com.fitnesscenter.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EquipmentRq {
    @NotNull(message = "Зона обязательна")
    private Long zoneId;

    @NotBlank(message = "Название оборудования обязательно")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    private String name;

    private String status;

    @NotNull(message = "Дата покупки обязательна")
    @Past(message = "Дата покупки должна быть в прошлом")
    private LocalDate dataBuy;
}