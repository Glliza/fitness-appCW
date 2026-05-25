package com.fitnesscenter.app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRs {
    private Long id;
    private String login;
    private String fio;
    private String email;
}