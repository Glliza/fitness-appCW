package com.fitnesscenter.app.controller;

import com.fitnesscenter.app.entity.Notification;
import com.fitnesscenter.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Получить все непрочитанные уведомления для администратора
    @GetMapping("/unread/{adminId}")
    public ResponseEntity<List<Notification>> getUnread(@PathVariable Long adminId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(adminId));
    }

    // Получить все уведомления для администратора
    @GetMapping("/{adminId}")
    public ResponseEntity<List<Notification>> getAll(@PathVariable Long adminId) {
        return ResponseEntity.ok(notificationService.getAllNotifications(adminId));
    }

    // Отметить уведомление как прочитанное
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // Отметить все уведомления как прочитанные
    @PatchMapping("/read-all/{adminId}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long adminId) {
        notificationService.markAllAsRead(adminId);
        return ResponseEntity.ok().build();
    }
}