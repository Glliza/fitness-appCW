package com.fitnesscenter.app.dto.request;


import lombok.Data;

@Data
public class RequestBuyRq {
    private Long equipmentInventoryNumber;
    private Integer count;
    private String name;
}
