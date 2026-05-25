package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    private Long adminId;

    private String title;

    private String message;

    private Boolean isRead = false;

    private LocalDateTime sentAt;
}