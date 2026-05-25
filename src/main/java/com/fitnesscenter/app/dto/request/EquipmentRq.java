package com.fitnesscenter.app.dto.request;


import lombok.Data;
import java.time.LocalDate;

@Data
public class EquipmentRq {
    private Long zoneId;
    private String name;
    private String status;
    private LocalDate dataBuy;
}
