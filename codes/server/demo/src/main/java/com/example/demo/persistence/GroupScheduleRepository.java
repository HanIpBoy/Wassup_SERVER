package com.example.demo.persistence;

import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.PersonalScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupScheduleRepository extends JpaRepository<GroupScheduleEntity, String> {

}

