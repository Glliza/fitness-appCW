package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumablesZonesRs {
    private Long consumablesId;
    private Long zoneId;
    private Integer count;
}
