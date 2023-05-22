package com.example.demo.persistence;

import com.example.demo.model.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    NotificationEntity findByNotificationId(String notificationId);
    void deleteByuserId(String userId);
}
