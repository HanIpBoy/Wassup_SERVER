package com.example.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import com.example.demo.model.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>{

	UserEntity findByUserId(String userId);
	boolean existsById(String userId);
	UserEntity findByUserNameAndPassword(String userId, String password);
	UserEntity findByUserName(String userId);
	List<UserEntity> findAll();
}
