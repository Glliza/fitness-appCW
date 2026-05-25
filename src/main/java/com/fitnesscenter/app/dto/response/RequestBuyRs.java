package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class RequestBuyRs {
    private Long id;
    private Long equipmentInventoryNumber;
    private Integer count;
    private String name;
    private LocalDate created_at;
    private String status;
}
