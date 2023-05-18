package com.example.demo.persistence;

import com.example.demo.model.UserScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserScheduleRepository extends JpaRepository<UserScheduleEntity, String> {

	List<UserScheduleEntity> findByUserId(String userId);
	UserScheduleEntity findByOriginKey(String originKey);
	List<UserScheduleEntity> findAllByUserIdOrderByStartAtAsc(String userId);
}

