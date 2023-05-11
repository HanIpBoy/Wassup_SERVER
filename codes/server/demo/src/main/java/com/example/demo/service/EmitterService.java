package com.example.demo.service;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.NotificationEntity;
import com.example.demo.persistence.EmitterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service

public class EmitterService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Autowired
    private EmitterRepository emitterRepository;

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter();

        emitterRepository.save(userId, emitter);

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, userId, "EventStream Created. [userId=" + userId + "]");

        return emitter;
    }

//    public void send(String receiverId, NotificationEntity notification, String content) {
//        NotificationEntity noti = createNotification(receiverId, notification, content);
//
//        // 로그인 한 유저의 SseEmitter 모두 가져오기
//        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(receiverId);
//        sseEmitters.forEach(
//                (key, emitter) -> {
//                    // 데이터 전송
//                    sendToClient(emitter, key, noti);
//                }
//        );
//    }
//
//    private NotificationEntity createNotification(String receiver, NotificationEntity notification, String content) {
//        return NotificationEntity.builder()
//                .receiver(receiver)
//                .content(content)
//                .notification(notification)
//                .url("/group/schedule/" + notification.getNotificationId())
//                .build();
//    }

    public void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("notification")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.delete(id);
            throw new RuntimeException("연결 오류!");
        }
    }

    public void sendToClients(List<NotificationEntity> notificationEntities, GroupEntity groupEntity) {
        for(NotificationEntity noti : notificationEntities) {
            // 각 groupUser마다 notificationDTO를 만듦, 지금 for문에서 leader 거는 존재X
            NotificationDTO dto = NotificationDTO.builder()
                    .group(groupEntity)
                    .notification(noti)
                    .build();
<<<<<<< HEAD
=======
            Optional<EmitterEntity> emitterEntity = emitterRepository.findByuserId(noti.getUserId());
>>>>>>> main

            Optional<SseEmitter> optionalEmitter = emitterRepository.get(noti.getUserId());
            optionalEmitter.ifPresent(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .id(noti.getUserId())
                            .name("notification")
                            .data(dto));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}