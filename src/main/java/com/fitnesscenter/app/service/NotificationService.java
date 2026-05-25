package com.fitnesscenter.app.service;

import com.fitnesscenter.app.entity.Notification;
import com.fitnesscenter.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void notifyAdmin(Long adminId, String title, String message) {
        Notification notification = new Notification();
        notification.setAdminId(adminId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);

        log.info("Уведомление для админа {}: {} - {}", adminId, title, message);
    }

    public List<Notification> getUnreadNotifications(Long adminId) {
        return notificationRepository.findByAdminIdAndIsReadFalse(adminId);
    }

    public List<Notification> getAllNotifications(Long adminId) {
        return notificationRepository.findByAdminIdOrderBySentAtDesc(adminId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long adminId) {
        List<Notification> unread = notificationRepository.findByAdminIdAndIsReadFalse(adminId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
}