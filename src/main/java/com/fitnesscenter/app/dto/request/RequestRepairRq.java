package com.fitnesscenter.app.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RequestRepairRq {

    @NotNull(message = "Оборудование обязательно")
    private Long equipmentInventoryNumber;

    @NotBlank(message = "Укажите, кто зафиксировал поломку")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String creator;

    @Size(max = 100, message = "Описание не должно превышать 100 символов")
    private String description;

    private Long TORepairId;
}