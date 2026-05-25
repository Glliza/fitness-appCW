package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "administrator")
public class Administrator extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    private String fio;

    private String email;

    private String role;
}