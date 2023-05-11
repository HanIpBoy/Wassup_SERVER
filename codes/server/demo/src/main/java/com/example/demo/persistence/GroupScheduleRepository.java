package com.example.demo.persistence;

import com.example.demo.model.GroupScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupScheduleRepository extends JpaRepository<GroupScheduleEntity, String> {
    List<GroupScheduleEntity> findByGroupOriginKey(String groupOriginKey);
}

