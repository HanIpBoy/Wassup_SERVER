package com.example.demo.service;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.persistence.EmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    public void sendToClients(List<NotificationDTO> dto) {
        for(NotificationDTO noti : dto) {
            String userId = noti.getNotification().getUserId();
            Optional<SseEmitter> optionalEmitter = emitterRepository.get(userId);
            optionalEmitter.ifPresent(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .id(userId)
                            .name("GroupScheduleCreateNotification")
                            .data(noti));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }



}