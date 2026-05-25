package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class EquipmentHistoryRs {
    private Long id;
    private String equipmentHistoryNumber;
    private String type;
    private LocalDate date;
    private String worker;
    private String description;
}
