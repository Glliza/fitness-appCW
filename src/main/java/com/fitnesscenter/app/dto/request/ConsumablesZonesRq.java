package com.fitnesscenter.app.dto.request;


import lombok.Data;

@Data
public class ConsumablesZonesRq {
    private Long consumablesId;
    private Long zoneId;
    private Integer count;
}
