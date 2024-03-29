package com.example.demo.persistence;

import com.example.demo.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, String> {

	GroupEntity findByOriginKey(String originKey);
	//List<GroupEntity> findAllByOrderedByStartAtAsc();
}

