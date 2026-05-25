package com.fitnesscenter.app.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZoneRs {
    private Long id;
    private Long userid;
    private String name;
    private String description;
    private Integer capacity;
    private Integer floor;
}