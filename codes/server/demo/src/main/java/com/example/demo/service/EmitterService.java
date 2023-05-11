package com.example.demo.service;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.model.EmitterEntity;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.NotificationEntity;
import com.example.demo.persistence.EmitterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service

public class EmitterService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Autowired
    private EmitterRepository emitterRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public SseEmitter subscribe(String userId) throws JsonProcessingException {
        EmitterEntity emitterEntity = new EmitterEntity();

        SseEmitter emitter = new SseEmitter();
        String emitterToJson = objectMapper.writeValueAsString(emitter);
        emitterEntity.setSseEmitter(emitterToJson);
        emitterEntity.setUserId(userId);
        emitterRepository.save(emitterEntity);

        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        emitter.onTimeout(() -> { emitter.complete();
            emitterRepository.deleteById(userId);
            });
        emitter.onError((e) -> { emitter.completeWithError(e);
                emitterRepository.deleteById(userId);
            });
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

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("notification")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결 오류!");
        }
    }

    public void sendToClients(List<NotificationEntity> notificationEntities, GroupEntity groupEntity) {
        for(NotificationEntity noti : notificationEntities) {
            // 각 groupUser마다 notificationDTO를 만듦, 지금 for문에서 leader의 notientity는 존재X
            NotificationDTO dto = NotificationDTO.builder()
                    .group(groupEntity)
                    .notification(noti)
                    .build();
            Optional<EmitterEntity> emitterEntity = emitterRepository.findByuserId(noti.getUserId());

            if (emitterEntity.isPresent()) {
                String jsonEmitter = emitterEntity.get().getSseEmitter();
                try {
                    SseEmitter emitter = objectMapper.readValue(jsonEmitter, SseEmitter.class);
                    emitter.send(SseEmitter.event()
                            .id(noti.getUserId())
                            .name("notification")
                            .data(dto)
                    );
                } catch (IOException e) {
                    // Exception 처리 (emitter를 json 파싱을 못했을때, 또는 emitter.send를 못했을때)
                    e.printStackTrace();
                }
            }
        }
    }
}