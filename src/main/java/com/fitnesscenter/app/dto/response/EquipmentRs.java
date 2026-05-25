package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class EquipmentRs {
    private Long id;
    private String zoneName;
    private String name;
    private String status;
    private LocalDate dataBuy;
}