package com.example.demo.service;

import com.example.demo.model.ScheduleEntity;
import com.example.demo.persistence.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository repository;

	public List<ScheduleEntity> create(final ScheduleEntity entity) {
		// Validations
		validate(entity);

		repository.save(entity);

		log.info("Entity id : {} is saved.", entity.getOrginKey());

		return repository.findByUserId(entity.getUserId());
	}

	public List<ScheduleEntity> retrieve(final String userId) {
		return repository.findByUserId(userId);
	}

	public List<ScheduleEntity> update(final ScheduleEntity entity) {
		validate(entity);

		final Optional<ScheduleEntity> original = repository.findById(entity.getOrginKey());

		original.ifPresent(schedule -> {
			schedule.setName(entity.getName());
			schedule.setStartAt(entity.getStartAt());
			schedule.setEndAt(entity.getEndAt());
			schedule.setMemo(entity.getMemo());
			schedule.setNotification(entity.getNotification());
			schedule.setAllDayToggle(entity.getAllDayToggle());
			repository.save(schedule);
		});

		return retrieve(entity.getUserId());
	}

	public List<ScheduleEntity> delete(final ScheduleEntity entity) {
		validate(entity);

		try {
			repository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getUserId(), e);

			throw new RuntimeException("error deleting entity " + entity.getUserId());
		}

		return retrieve(entity.getUserId());
	}

	private void validate(final ScheduleEntity entity) {
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
