package com.fitnesscenter.app.dto.request;


import lombok.Data;
import java.time.LocalDate;

@Data
public class TORepairRq {
    private Long equipmentId;
    private String type;
    private LocalDate plannedDate;
    private String description;
    private String worker;
}