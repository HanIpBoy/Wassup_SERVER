package com.example.demo.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class EmitterRepository {

    // 유저ID를 키로 SseEmitter를 해시맵에 저장할 수 있도록 구현했다.
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void save(String userId, SseEmitter sseEmitter) {
        emitterMap.put(userId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterMap.remove(userId));
        sseEmitter.onTimeout(() -> emitterMap.remove(userId));
        sseEmitter.onError((e) -> { sseEmitter.completeWithError(e);
            emitterMap.remove(userId);
        });
        log.info("Saved SseEmitter for {}", userId);
    }

    public Optional<SseEmitter> get(String userId) {
        log.info("Got SseEmitter for {}", userId);
        return Optional.ofNullable(emitterMap.get(userId));
    }

    public void delete(String userId) {
        emitterMap.remove(userId);
        log.info("Deleted SseEmitter for {}", userId);
    }

    public Collection<SseEmitter> getAllEmitters() {
        return emitterMap.values();
    }

}