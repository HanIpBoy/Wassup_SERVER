package com.example.demo.persistence;

import com.example.demo.model.PersonalScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<PersonalScheduleEntity, String> {

	List<PersonalScheduleEntity> findByUserId(String userId);
	Optional<PersonalScheduleEntity> findByOriginKey(String originKey);
	List<PersonalScheduleEntity> findAllByUserIdOrderByStartAtAsc(String userId);
	List<PersonalScheduleEntity> findByGroupOriginKey(String groupOriginKey);
}

