package com.example.demo.service;

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

    public List<NotificationDTO> createGroupInviteNotification(List<String> groupUsers,String groupOriginKey) {
        List<NotificationDTO> dtos = new ArrayList<>();

        GroupEntity entity = groupRepository.findByOriginKey(groupOriginKey);

        for (String user : groupUsers) {
            if (user.equals(entity.getLeaderId())) continue;

            NotificationEntity notiEntity = NotificationEntity.builder()
                    .userId(user)
                    .title("그룹 초대")
                    .message(entity.getLeaderId() + " 님이 당신을 " + entity.getGroupName() + " 그룹에 초대하셨습니다.")
                    .build();

            notificationRepository.save(notiEntity);

            NotificationDTO dto = NotificationDTO.builder()
                    .groupOriginKey(entity.getOriginKey())
                    .notification(notiEntity)
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public List<NotificationDTO> createGroupScheduleNotification(GroupScheduleEntity groupScheduleEntity,String notiType) {
        List<NotificationDTO> dtos = new ArrayList<>();

        // groupUserEntities 꺼내기
        List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(groupScheduleEntity.getGroupOriginKey());

        // 그룹 정보 가져오기
        GroupEntity group = groupRepository.findByOriginKey(groupUserEntities.get(0).getGroupOriginKey());

        // 루프 돌면서 notificationDTO 만드는 과정
        for(GroupUserEntity groupUser : groupUserEntities) {
            NotificationEntity notiEntity = null;

            // notificationEntity 만들기
            switch (notiType) {
                case "Create":
                     notiEntity = NotificationEntity.builder()
                            .userId(groupUser.getUserId())
                            .title("그룹 일정 생성")
                            .message(group.getGroupName() + " 그룹에서 " + groupScheduleEntity.getName() + " 일정을 생성하였습니다.")
                            .build();
                    break;
                case "Update":
                    notiEntity = NotificationEntity.builder()
                            .userId(groupUser.getUserId())
                            .title("그룹 일정 수정")
                            .message(group.getGroupName() + " 그룹에서 " + groupScheduleEntity.getName() + " 일정을 수정하였습니다.")
                            .build();
                    break;
                case "Delete":
                    notiEntity = NotificationEntity.builder()
                            .userId(groupUser.getUserId())
                            .title("그룹 일정 삭제")
                            .message(group.getGroupName() + " 그룹에서 " + groupScheduleEntity.getName() + " 일정을 삭제하였습니다.")
                            .build();
                    break;
                default:
                    // 예외 처리 등을 수행하거나 기본값 할당
                    break;
            }
            // notificationEntities와 GroupEntity를 병합해 NotificationDTO 만들기
            NotificationDTO dto = NotificationDTO.builder()
                    .groupOriginKey(group.getOriginKey())
                    .notification(notiEntity)
                    .build();
            dtos.add(dto);
        }

        return dtos;
    }
    public NotificationEntity retreieve(String originKey){
        return notificationRepository.findByNotificationId(originKey);
    }

    public void deleteNotification(NotificationEntity entity) {
        notificationRepository.delete(entity);
    }

    public void deleteNotificationByUserId(String userId) {
        notificationRepository.deleteByuserId(userId);
    }
}
