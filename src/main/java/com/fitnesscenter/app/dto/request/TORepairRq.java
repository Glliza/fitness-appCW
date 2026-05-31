package com.fitnesscenter.app.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TORepairRq {

    @NotNull(message = "Оборудование обязательно")
    private Long equipmentId;

    @NotBlank(message = "Тип ТО обязателен")
    private String type;

    @NotNull(message = "Плановая дата обязательна")
    @Future(message = "Дата ТО должна быть в будущем")
    private LocalDate plannedDate;

    @Size(max = 100, message = "Описание не должно превышать 100 символов")
    private String description;

    private String worker;
}