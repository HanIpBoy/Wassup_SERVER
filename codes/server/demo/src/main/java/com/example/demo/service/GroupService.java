package com.example.demo.service;

import com.example.demo.model.GroupEntity;
import com.example.demo.persistence.GroupRepository;
import com.example.demo.persistence.GroupUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupService {

	@Autowired
	private GroupRepository repository;

	@Autowired
	private GroupUserRepository guRepository;

	public List<GroupEntity> create(final GroupEntity entity) {
		// Validations
		validate(entity);

		repository.save(entity);

		log.info("Entity id : {} is saved.", entity.getOriginKey());

		return repository.findByOriginKey(entity.getOriginKey());
	}

	public List<GroupEntity> retrieve(final String userId) {
		return repository.findAllByOrderedByStartAtAsc();
	}

	public List<GroupEntity> update(final GroupEntity entity) {
		validate(entity);

		final Optional<GroupEntity> original = repository.findByOriginKey(entity.getOriginKey());

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

	public List<GroupEntity> delete(final GroupEntity entity) {
		validate(entity);

		try {
			repository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getUserId(), e);

			throw new RuntimeException("error deleting entity " + entity.getUserId());
		}

		return retrieve(entity.getUserId());
	}

	private void validate(final GroupEntity entity) {
		if(entity == null) {
			log.warn("Entity cannot be null");
			throw new RuntimeException("Entity cannot be null");
		}

		if(entity.getLeaderId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}

	}
}
