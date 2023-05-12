package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.UserScheduleEntity;
import com.example.demo.service.EmitterService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("group/schedule")
public class GroupScheduleController {
    @Autowired
    private ScheduleService service;

    @Autowired
    private EmitterService emitterService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> createGroupSchedule(@RequestBody GroupScheduleDTO dto) {
        try {
            GroupScheduleEntity entity = GroupScheduleDTO.toEntity(dto);

            // 그룹 일정 생성
            GroupScheduleEntity groupScheduleEntity = service.createGroupSchedule(entity);

            // 그룹원들에게 그룹 일정 생성 알림을 보내려면, 필요한 정보가 그룹에 대한 정보와 일정에 대한 정보
            List<NotificationDTO> notificationDTOs = notificationService.createGroupScheduleNotification(groupScheduleEntity,"Create");

            // NotificationDTO를 만들어 SseEmitter로 요청 알림 전송
            emitterService.sendToClients(notificationDTOs);

            ResponseDTO<?> response = ResponseDTO.<GroupDTO>builder().status("succeed").build();

            return ResponseEntity.ok().body(response);

        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<GroupScheduleDTO> response = ResponseDTO.<GroupScheduleDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveGroupSchedule(@RequestBody GroupScheduleDTO dto) {
        try {
            GroupScheduleEntity entity = GroupScheduleDTO.toEntity(dto);

            List<GroupScheduleEntity> entities = service.retrieveGroupSchedule(dto.getOriginKey());

            List<GroupScheduleDTO> dtos = entities.stream().map(GroupScheduleDTO::new).collect(Collectors.toList());

            ResponseDTO response = ResponseDTO.<GroupScheduleDTO>builder().data(dtos).status("succeed").build();

            return ResponseEntity.ok().body(response);

        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<GroupScheduleDTO> response = ResponseDTO.<GroupScheduleDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateGroupSchedule(@RequestBody GroupScheduleDTO dto) {

        GroupScheduleEntity entity = GroupScheduleDTO.toEntity(dto);
        //그룹 스케쥴 수정
        entity = service.updateGroupSchedule(entity);

        // 그룹원들에게 그룹 일정 수정 알림을 보내려면, 필요한 정보가 그룹에 대한 정보와 일정에 대한 정보
        List<NotificationDTO> notificationDTOs = notificationService.createGroupScheduleNotification(entity,"Update");

        // NotificationDTO를 만들어 SseEmitter로 요청 알림 전송
        emitterService.sendToClients(notificationDTOs);

        ResponseDTO<?> response = ResponseDTO.<GroupDTO>builder().status("succeed").build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteGroupSchedule(@RequestBody GroupScheduleDTO dto) {
        try {
            GroupScheduleEntity entity = GroupScheduleDTO.toEntity(dto);

            entity = service.deleteGroupSchedule(entity);

            List<NotificationDTO> notificationDTOs = notificationService.createGroupScheduleNotification(entity,"Delete");

            // NotificationDTO를 만들어 SseEmitter로 요청 알림 전송
            emitterService.sendToClients(notificationDTOs);

            ResponseDTO<?> response = ResponseDTO.<GroupDTO>builder().status("succeed").build();

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO response = ResponseDTO.<UserScheduleDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
