package com.example.demo.persistence;

import com.example.demo.model.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    NotificationEntity findByOriginKey(String notificationOriginKey);
    void deleteAllByUserId(String userId);

    List<NotificationEntity> findByUserId(String userId);

}
