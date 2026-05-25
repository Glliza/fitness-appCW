package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestRepairRs {
    private Long id;
    private Long TORepairId;
    private Long equipmentInventoryNumber;
    private LocalDateTime created_at;
    private String status;
    private String worker;
    private String description;
    private String creator;
}
