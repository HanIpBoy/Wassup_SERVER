package com.example.demo.persistence;

import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUserEntity, String> {

	List<GroupUserEntity> findByUserId(String userId);
	Optional<GroupUserEntity> findByOriginKey(String originKey);
	List<GroupUserEntity> findAllByOrderedByStartAtAsc();
}

