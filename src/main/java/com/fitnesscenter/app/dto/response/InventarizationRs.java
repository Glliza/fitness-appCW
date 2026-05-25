package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class InventarizationRs {
    private Long id;
    private Long equipmentInventoryNumber;
    private Integer count;
    private Integer realCount;
    private LocalDate date;
}
