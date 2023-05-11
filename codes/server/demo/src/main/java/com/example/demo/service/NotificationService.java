package com.example.demo.service;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.NotificationDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.GroupUserEntity;
import com.example.demo.model.NotificationEntity;
import com.example.demo.persistence.GroupRepository;
import com.example.demo.persistence.GroupUserRepository;
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

    @Autowired
    private GroupUserRepository groupUserRepository;

    @Autowired
    private GroupRepository groupRepository;

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

    public List<NotificationDTO> createGroupScheduleNotification(GroupScheduleEntity groupScheduleEntity) {
        List<NotificationDTO> dtos = new ArrayList<>();

        // groupUserEntities 꺼내기
        List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(groupScheduleEntity.getGroupOriginKey());

        // 그룹 정보 가져오기
        GroupEntity group = groupRepository.findByOriginKey(groupUserEntities.get(0).getGroupOriginKey());

        // 루프 돌면서 notificationDTO 만드는 과정
        for(GroupUserEntity groupUser : groupUserEntities) {
            // notificationEntity 만들기
            NotificationEntity notiEntity = NotificationEntity.builder()
                    .userId(groupUser.getUserId())
                    .title("그룹 일정 생성")
                    .message(group.getGroupName() + " 그룹에서 " + groupScheduleEntity.getName() + " 일정을 생성하였습니다.")
                    .build();

            // notificationEntities와 GroupEntity를 병합해 NotificationDTO 만들기
            NotificationDTO dto = NotificationDTO.builder()
                    .group(group)
                    .notification(notiEntity)
                    .build();
            dtos.add(dto);
        }

        return dtos;
    }

    public void deleteNotification(NotificationEntity entity) {
        notificationRepository.delete(entity);
    }
}
