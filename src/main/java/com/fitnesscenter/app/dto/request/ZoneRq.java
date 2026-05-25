package com.fitnesscenter.app.dto.request;


import lombok.Data;

@Data
public class ZoneRq {
    private Long userid;
    private String name;
    private String description;
    private Integer capacity;
    private Integer floor;
}