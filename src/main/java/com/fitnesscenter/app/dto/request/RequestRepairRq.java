package com.fitnesscenter.app.dto.request;


import lombok.Data;

@Data
public class RequestRepairRq {
    private Long equipmentInventoryNumber;
    private String creator;
    private Long TORepairId;
}
