package com.example.demo.service;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.GroupUserEntity;
import com.example.demo.model.NotificationEntity;
import com.example.demo.persistence.GroupRepository;
import com.example.demo.persistence.GroupUserRepository;
import com.example.demo.persistence.NotificationRepository;
import com.example.demo.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserRepository userRepository;

    public List<NotificationDTO> createGroupInviteNotification(List<String> groupUsers, String groupOriginKey) {
        List<NotificationDTO> dtos = new ArrayList<>();

        GroupEntity entity = groupRepository.findByOriginKey(groupOriginKey);
        String groupLeaderName = userRepository.findByUserId(entity.getLeaderId()).getUserName();

        //그룹장 아이디 가져오기
        String leaderId = entity.getLeaderId();

        for (String user : groupUsers) {
            //그룹장은 알림을 생성하지 않음
            if (user.equals(leaderId)) continue;

            NotificationEntity notiEntity = NotificationEntity.builder()
                    .userId(user)
                    .title("그룹 초대")
                    .message(groupLeaderName+"("+entity.getLeaderId() + ") 님이 당신을 \"" + entity.getGroupName() + "\" 그룹에 초대하셨습니다.")
                    .groupOriginKey(groupOriginKey)
                    .build();

            notificationRepository.save(notiEntity);

            NotificationDTO dto = new NotificationDTO(notiEntity);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<NotificationDTO> createNotificationForGroupScheduleCreate(GroupScheduleEntity groupScheduleEntity) {
        List<NotificationDTO> dtos = new ArrayList<>();

        // groupUserEntities 꺼내기
        List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(groupScheduleEntity.getGroupOriginKey());

        // 그룹 정보 가져오기
        GroupEntity group = groupRepository.findByOriginKey(groupUserEntities.get(0).getGroupOriginKey());

        //그룹장 아이디 가져오기
        String leaderId = group.getLeaderId();

        // 루프 돌면서 notificationDTO 만드는 과정
        for (GroupUserEntity groupUser : groupUserEntities) {

            //그룹장은 알림을 생성하지 않음
            if (groupUser.getUserId().equals(leaderId)) continue;

            NotificationEntity notiEntity = null;
            notiEntity = NotificationEntity.builder()
                    .userId(groupUser.getUserId())
                    .title("그룹 일정 생성")
                    .message("\""+group.getGroupName() + "\" 그룹에서 \"" + groupScheduleEntity.getName() + "\" 일정을 생성하였습니다.")
                    .groupOriginKey(groupUser.getGroupOriginKey())
                    .build();
            notificationRepository.save(notiEntity);
            dtos.add(new NotificationDTO(notiEntity));
        }
        return dtos;
    }

    public List<NotificationDTO> createNotificationForGroupScheduleUpdate(GroupScheduleEntity groupScheduleEntity) {
        List<NotificationDTO> dtos = new ArrayList<>();

        // groupUserEntities 꺼내기
        List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(groupScheduleEntity.getGroupOriginKey());

        // 그룹 정보 가져오기
        GroupEntity group = groupRepository.findByOriginKey(groupUserEntities.get(0).getGroupOriginKey());

        //그룹장 아이디 가져오기
        String leaderId = group.getLeaderId();

        // 루프 돌면서 notificationDTO 만드는 과정
        for (GroupUserEntity groupUser : groupUserEntities) {

            //그룹장은 알림을 생성하지 않음
            if (groupUser.getUserId().equals(leaderId)) continue;

            NotificationEntity notiEntity = null;
            notiEntity = NotificationEntity.builder()
                    .userId(groupUser.getUserId())
                    .title("그룹 일정 수정")
                    .message("\""+group.getGroupName() + "\" 그룹에서 \"" + groupScheduleEntity.getName() + "\" 일정을 수정하였습니다.")
                    .groupOriginKey(groupUser.getGroupOriginKey())
                    .build();
            notificationRepository.save(notiEntity);
            dtos.add(new NotificationDTO(notiEntity));
        }
        return dtos;
    }

    public List<NotificationDTO> createNotificationForGroupScheduleDelete(GroupScheduleEntity groupScheduleEntity) {
        List<NotificationDTO> dtos = new ArrayList<>();

        // groupUserEntities 꺼내기
        List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(groupScheduleEntity.getGroupOriginKey());

        // 그룹 정보 가져오기
        GroupEntity group = groupRepository.findByOriginKey(groupUserEntities.get(0).getGroupOriginKey());

        //그룹장 아이디 가져오기
        String leaderId = group.getLeaderId();

        // 루프 돌면서 notificationDTO 만드는 과정
        for (GroupUserEntity groupUser : groupUserEntities) {

            //그룹장은 알림을 생성하지 않음
            if (groupUser.equals(leaderId)) continue;

            NotificationEntity notiEntity = null;
            notiEntity = NotificationEntity.builder()
                    .userId(groupUser.getUserId())
                    .title("그룹 일정 삭제")
                    .message("\""+group.getGroupName() + "\" 그룹에서 \"" + groupScheduleEntity.getName() + "\" 일정을 삭제하였습니다.")
                    .groupOriginKey(groupUser.getGroupOriginKey())
                    .build();
            notificationRepository.save(notiEntity);
            dtos.add(new NotificationDTO(notiEntity));
        }
        return dtos;
    }

    public NotificationEntity retreieveByOriginKey(String originKey) {
        return notificationRepository.findByOriginKey(originKey);
    }

    public List<NotificationEntity> retrieveByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public void deleteNotification(NotificationEntity entity) {
        notificationRepository.delete(entity);
    }

    @Transactional
    public void deleteNotificationByUserId(String userId) {
        notificationRepository.deleteByUserId(userId);
    }
}
