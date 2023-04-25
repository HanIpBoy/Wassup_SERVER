package com.example.demo.persistence;

import com.example.demo.model.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, String> {

	List<ScheduleEntity> findByUserId(String userId);
	Optional<ScheduleEntity> findByOriginKey(String originKey);
	List<ScheduleEntity> findAllByOrderedByStartAtAsc();
}

