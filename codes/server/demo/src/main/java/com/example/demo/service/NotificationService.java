package com.example.demo.service;

import com.example.demo.dto.GroupDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.NotificationEntity;
import com.example.demo.persistence.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<NotificationEntity> createGroupInviteNotification(List<String> groupUsers, GroupEntity entity) {
        List<NotificationEntity> entities = new ArrayList<>();

        for (String user : groupUsers) {
            if (user.equals(entity.getLeaderId())) continue;

            NotificationEntity notiEntity = NotificationEntity.builder()
                    .userId(user)
                    .title("그룹 초대")
                    .message(entity.getLeaderId() + " 님이 당신을 " + entity.getGroupName() + " 그룹에 초대하셨습니다.")
                    .build();

            notificationRepository.save(notiEntity);
            entities.add(notiEntity);
        }
        return entities;
    }



    public void deleteNotification(NotificationEntity entity) {
        notificationRepository.delete(entity);
    }
}
