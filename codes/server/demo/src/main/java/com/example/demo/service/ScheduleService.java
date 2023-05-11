package com.example.demo.service;

import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.UserScheduleEntity;
import com.example.demo.persistence.EmitterRepository;
import com.example.demo.persistence.GroupScheduleRepository;
import com.example.demo.persistence.GroupUserRepository;
import com.example.demo.persistence.UserScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScheduleService {

	@Autowired
	private UserScheduleRepository userScheduleRepository;

	@Autowired
	private GroupScheduleRepository groupScheduleRepository;

	public List<UserScheduleEntity> createUserSchedule(final UserScheduleEntity entity) {
		// Validations
		validate(entity);

		userScheduleRepository.save(entity);

		log.info("Entity id : {} is saved.", entity.getOriginKey());

		return userScheduleRepository.findByUserId(entity.getUserId());
	}

	public GroupScheduleEntity createGroupSchedule(GroupScheduleEntity entity) {
		groupScheduleRepository.save(entity);

		log.info("Entity id : {} is saved.", entity.getOriginKey());

		return entity;
	}

	public List<UserScheduleEntity> retrieveUserSchedule(final String userId) {

		return userScheduleRepository.findAllByUserIdOrderByStartAtAsc(userId);
	}

	public List<GroupScheduleEntity> retrieveGroupSchedule(final String groupOriginKey){
		return groupScheduleRepository.findByGroupOriginKey(groupOriginKey);
	}

	public List<UserScheduleEntity> updateUserSchedule(final UserScheduleEntity entity) {
		validate(entity);

		final Optional<UserScheduleEntity> original = userScheduleRepository.findByOriginKey(entity.getOriginKey());

		original.ifPresent(schedule -> {
			schedule.setName(entity.getName() != null ? entity.getName() : schedule.getName());
			schedule.setStartAt(entity.getStartAt() != null ? entity.getStartAt() : schedule.getStartAt());
			schedule.setEndAt(entity.getEndAt() != null ? entity.getEndAt() : schedule.getEndAt());
			schedule.setMemo(entity.getMemo() != null ? entity.getMemo() : schedule.getMemo());
			schedule.setAllDayToggle(entity.getAllDayToggle() != null ? entity.getAllDayToggle() : schedule.getAllDayToggle());
			userScheduleRepository.save(schedule);
		});

		return retrieveUserSchedule(entity.getUserId());
	}

	public List<GroupScheduleEntity> updateGroupSchedule(final GroupScheduleEntity entity) {
		//validate(entity);
		final Optional<GroupScheduleEntity> original =groupScheduleRepository.findById(entity.getOriginKey());

		original.ifPresent(groupSchedule ->{
			groupSchedule.setGroupOriginKey(entity.getGroupOriginKey()!= null ? entity.getGroupOriginKey() : groupSchedule.getGroupOriginKey());
			groupSchedule.setStartAt(entity.getStartAt() != null ? entity.getStartAt() : groupSchedule.getStartAt());
			groupSchedule.setEndAt(entity.getEndAt() != null ? entity.getEndAt() : groupSchedule.getEndAt());
			groupSchedule.setMemo(entity.getMemo() != null ? entity.getMemo() : groupSchedule.getMemo());
			groupSchedule.setAllDayToggle(entity.getAllDayToggle() != null ? entity.getAllDayToggle() : groupSchedule.getAllDayToggle());
			groupSchedule.setColor(entity.getColor() != null ? entity.getColor() : groupSchedule.getColor());
			groupScheduleRepository.save(groupSchedule);
		});

		return retrieveGroupSchedule(entity.getGroupOriginKey());
	}

	public List<UserScheduleEntity> deleteUserSchedule(final UserScheduleEntity entity) {
		validate(entity);

		try {
			userScheduleRepository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getUserId(), e);

			throw new RuntimeException("error deleting entity " + entity.getUserId());
		}

		return retrieveUserSchedule(entity.getUserId());
	}

	private void validate(final UserScheduleEntity entity) {
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
