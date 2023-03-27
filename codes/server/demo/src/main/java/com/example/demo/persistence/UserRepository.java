package com.example.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>{
	
	UserEntity findByUserId(String userId);
	boolean existsById(String userId);
	UserEntity findByUserNameAndPassword(String userName, String password);

}
