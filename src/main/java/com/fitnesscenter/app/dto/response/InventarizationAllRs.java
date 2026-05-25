package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class InventarizationAllRs {
    private Long zoneId;
    private String zoneName;
    private List<InventarizationRs> items;
}
