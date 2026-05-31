package com.fitnesscenter.app.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestBuyRq {
    private Long equipmentInventoryNumber;
    @NotBlank(message = "Наименование обязательно")
    @Size(min = 2, max = 50, message = "Наименование должно быть от 2 до 50 символов")
    private String name;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть больше 0")
    private Integer count;
    }
