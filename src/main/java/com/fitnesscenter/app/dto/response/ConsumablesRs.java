package com.fitnesscenter.app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumablesRs {
    private Long id;
    private String name;
    private Integer realCount;
}
