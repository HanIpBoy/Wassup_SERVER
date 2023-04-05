package com.example.demo.persistence;

import com.example.demo.model.ScheduleEntity;
import com.example.demo.model.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, String> {

	List<ScheduleEntity> findByUserId(String userId);

}

