package com.example.demo.service;

import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.GroupUserEntity;
import com.example.demo.model.UserScheduleEntity;
import com.example.demo.persistence.GroupScheduleRepository;
import com.example.demo.persistence.GroupUserRepository;
import com.example.demo.persistence.UserScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScheduleService {

	@Autowired
	private UserScheduleRepository userScheduleRepository;

	@Autowired
	private GroupScheduleRepository groupScheduleRepository;

	@Autowired
	private GroupUserRepository groupUserRepository;

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

	//유저가 생성한 모든 스케쥴 검색 + 자신이 속해있는 그룹의 일정까지 전부 검색
	public List<UserScheduleEntity> retrieveUserSchedules(final String userId) {
		return userScheduleRepository.findAllByUserIdOrderByStartAtAsc(userId);
	}

	//그룹이 생성한 모든 스케쥴 검색
	public List<GroupScheduleEntity> retrieveGroupSchedule(final String groupOriginKey){
		return groupScheduleRepository.findByGroupOriginKey(groupOriginKey);
	}

	//사용자가 속한 모든 그룹의 일정들을 리스트로 반환
	public List<GroupScheduleEntity> retrieveGroupScheduleByUserId(final String userId) {
		List<GroupUserEntity> groupUsers = groupUserRepository.findByUserId(userId);
		List<GroupScheduleEntity> groupSchedules = new ArrayList<>();

		for(GroupUserEntity users : groupUsers) {
			for(GroupScheduleEntity schedule : retrieveGroupSchedule(users.getGroupOriginKey())) {
				groupSchedules.add(schedule);
			}
		}
		return groupSchedules;
	}

	public UserScheduleEntity retrieveUserSchedule(final String originKey) {
		return userScheduleRepository.findByOriginKey(originKey);
	}

	public List<UserScheduleEntity> updateUserSchedule(final UserScheduleEntity entity) {
		validate(entity);

		final Optional<UserScheduleEntity> original = Optional.ofNullable(userScheduleRepository.findByOriginKey(entity.getOriginKey()));

		original.ifPresent(schedule -> {
			schedule.setName(entity.getName() != null ? entity.getName() : schedule.getName());
			schedule.setStartAt(entity.getStartAt() != null ? entity.getStartAt() : schedule.getStartAt());
			schedule.setEndAt(entity.getEndAt() != null ? entity.getEndAt() : schedule.getEndAt());
			schedule.setMemo(entity.getMemo() != null ? entity.getMemo() : schedule.getMemo());
			schedule.setAllDayToggle(entity.getAllDayToggle() != null ? entity.getAllDayToggle() : schedule.getAllDayToggle());
			userScheduleRepository.save(schedule);
		});

		return retrieveUserSchedules(entity.getUserId());
	}

	public GroupScheduleEntity updateGroupSchedule(final GroupScheduleEntity entity) {
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

		return groupScheduleRepository.findByOriginKey(entity.getOriginKey());
	}

	public List<UserScheduleEntity> deleteUserSchedule(final UserScheduleEntity entity) {
		validate(entity);

		try {
			userScheduleRepository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getUserId(), e);

			throw new RuntimeException("error deleting entity " + entity.getUserId());
		}

		return retrieveUserSchedules(entity.getUserId());
	}

	public GroupScheduleEntity deleteGroupSchedule(final GroupScheduleEntity entity) {

		GroupScheduleEntity target = groupScheduleRepository.findByOriginKey(entity.getOriginKey());

		try {
			groupScheduleRepository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getOriginKey(), e);

			throw new RuntimeException("error deleting entity " + entity.getOriginKey());
		}
		return target;
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
