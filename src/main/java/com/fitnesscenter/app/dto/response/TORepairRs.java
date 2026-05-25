package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TORepairRs {
    private Long id;
    private String status;
    private String description;
    private String worker;
    private LocalDate plannedDate;
    private LocalDate completedDate;
    private String type;
}