package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAdminIdAndIsReadFalse(Long adminId);
    List<Notification> findByAdminIdOrderBySentAtDesc(Long adminId);
}