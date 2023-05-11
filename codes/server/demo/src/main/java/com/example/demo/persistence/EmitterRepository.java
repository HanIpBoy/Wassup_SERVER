package com.example.demo.persistence;

import com.example.demo.model.EmitterEntity;
import com.example.demo.model.GroupEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public interface EmitterRepository extends JpaRepository<EmitterEntity, String> {
    Optional<EmitterEntity> findByuserId(String userId);

//
//    // 유저ID를 키로 SseEmitter를 해시맵에 저장할 수 있도록 구현했다.
//    private Map<String, SseEmitter> emitterMap = new HashMap<>();
//
//    public SseEmitter save(String userId, SseEmitter sseEmitter) {
//        emitterMap.put(userId, sseEmitter);
//        log.info("Saved SseEmitter for {}", userId);
//        return sseEmitter;
//    }
//
//    public Optional<SseEmitter> get(Long userId) {
//        log.info("Got SseEmitter for {}", userId);
//        return Optional.ofNullable(emitterMap.get(userId));
//    }
//
//    public void delete(Long userId) {
//        emitterMap.remove(userId);
//        log.info("Deleted SseEmitter for {}", userId);
//    }
//
//    public void deleteById(String userId) {
//        emitterMap.remove(userId);
//    }
//
////    public Map<String, SseEmitter> findAllStartWithById(String receiverId) {
////        return emitterMap.get(receiverId);
////    }
    
    
}