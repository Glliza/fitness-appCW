package com.fitnesscenter.app.dto.request;

import lombok.Data;

@Data
public class RequestRepairRq {
    private Long equipmentInventoryNumber;
    private String creator;  // кто зафиксировал поломку
    private String description;  // описание поломки
    private Long TORepairId;
}