package com.example.demo.service;

import com.example.demo.model.GroupUserEntity;
import com.example.demo.model.PersonalScheduleEntity;
import com.example.demo.persistence.EmitterRepository;
import com.example.demo.persistence.GroupUserRepository;
import com.example.demo.persistence.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository repository;

	@Autowired
	private GroupUserRepository groupUserRepository;

	@Autowired
	private EmitterRepository emitterRepository;

	@Autowired
	private EmitterService emitterService;

	public List<PersonalScheduleEntity> create(final PersonalScheduleEntity entity) {
		// Validations
		validate(entity);

		repository.save(entity);

		log.info("Entity id : {} is saved.", entity.getOriginKey());

		return repository.findByUserId(entity.getUserId());
	}

	public List<PersonalScheduleEntity> createGroupSchedule(PersonalScheduleEntity entity) {
		// Validations
		validate(entity);

		// groupOriginKey를 받아서 이로 groupUsers를 찾아냄
		List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(entity.getGroupOriginKey());

		// 루프를 돌면서 groupUserId로 entity 세팅해서 Repo에 저장
		for(GroupUserEntity e : groupUserEntities) {
			entity.setUserId(e.getUserId());
			repository.save(entity);
			Optional<SseEmitter> emitter = emitterRepository.get(e.getUserId());
			emitter.ifPresent(emt -> {
				emitterService.sendToClient(emt, e.getUserId(), entity);
			});
		}

		log.info("Entity id : {} is saved.", entity.getOriginKey());

		return repository.findByGroupOriginKey(entity.getGroupOriginKey());
	}

	public List<PersonalScheduleEntity> retrieve(final String userId) {

		return repository.findAllByUserIdOrderByStartAtAsc(userId);
	}

	public List<PersonalScheduleEntity> update(final PersonalScheduleEntity entity) {
		validate(entity);

		final Optional<PersonalScheduleEntity> original = repository.findByOriginKey(entity.getOriginKey());

		original.ifPresent(schedule -> {
			schedule.setName(entity.getName() != null ? entity.getName() : schedule.getName());
			schedule.setStartAt(entity.getStartAt() != null ? entity.getStartAt() : schedule.getStartAt());
			schedule.setEndAt(entity.getEndAt() != null ? entity.getEndAt() : schedule.getEndAt());
			schedule.setMemo(entity.getMemo() != null ? entity.getMemo() : schedule.getMemo());
			schedule.setNotification(entity.getNotification() != null ? entity.getNotification() : schedule.getNotification());
			schedule.setAllDayToggle(entity.getAllDayToggle() != null ? entity.getAllDayToggle() : schedule.getAllDayToggle());
			repository.save(schedule);
		});

		return retrieve(entity.getUserId());
	}

	public List<PersonalScheduleEntity> updateGroupSchedule(final PersonalScheduleEntity entity) {
		validate(entity);

		List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(entity.getGroupOriginKey());

		for(GroupUserEntity e : groupUserEntities) {
			repository.findByOriginKey();

			Optional<SseEmitter> emitter = emitterRepository.get(e.getUserId());
			emitter.ifPresent(emt -> {
				emitterService.sendToClient(emt, e.getUserId(), entity);
			});
		}
		final Optional<PersonalScheduleEntity> original = repository.findByOriginKey(entity.getOriginKey());

		original.ifPresent(schedule -> {
			schedule.setName(entity.getName() != null ? entity.getName() : schedule.getName());
			schedule.setStartAt(entity.getStartAt() != null ? entity.getStartAt() : schedule.getStartAt());
			schedule.setEndAt(entity.getEndAt() != null ? entity.getEndAt() : schedule.getEndAt());
			schedule.setMemo(entity.getMemo() != null ? entity.getMemo() : schedule.getMemo());
			schedule.setNotification(entity.getNotification() != null ? entity.getNotification() : schedule.getNotification());
			schedule.setAllDayToggle(entity.getAllDayToggle() != null ? entity.getAllDayToggle() : schedule.getAllDayToggle());
			repository.save(schedule);
		});

		return retrieve(entity.getUserId());
	}

	public List<PersonalScheduleEntity> delete(final PersonalScheduleEntity entity) {
		validate(entity);

		try {
			repository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getUserId(), e);

			throw new RuntimeException("error deleting entity " + entity.getUserId());
		}

		return retrieve(entity.getUserId());
	}

	private void validate(final PersonalScheduleEntity entity) {
		if(entity == null) {
			log.warn("Entity cannot be null");
			throw new RuntimeException("Entity cannot be null");
		}

		if(entity.getUserId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}

	}
}
