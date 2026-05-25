package com.fitnesscenter.app.dto.request;

import lombok.Data;

@Data
public class AuthRq {
    private String login;
    private String password;
}