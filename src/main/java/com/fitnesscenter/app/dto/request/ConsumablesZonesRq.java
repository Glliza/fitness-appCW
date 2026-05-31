package com.fitnesscenter.app.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConsumablesZonesRq {
    @NotNull(message = "Расходный материал обязателен")
    private Long consumablesId;

    @NotNull(message = "Зона обязательна")
    private Long zoneId;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть больше 0")
    private Integer count;
}
