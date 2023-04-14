package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.demo.model.UserEntity;
import com.example.demo.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public UserEntity create(final UserEntity userEntity) {
		if(userEntity == null || userEntity.getUserId() == null ) {
			throw new RuntimeException("Invalid arguments");
		}
		final String id = userEntity.getUserId();
		if(userRepository.existsById(id)) {
			log.warn("id already exists {}", id);
			throw new RuntimeException("id already exists");
		}
		log.info("id 생성 완료! " + userEntity);
		return userRepository.save(userEntity);
	}

//	public UserEntity update(final UserEntity userEntity) {
//		userEntity.set
//	}
	public UserEntity getByUserId(String userId) {
		return userRepository.findByUserId(userId);
	}

	public UserEntity getByCredentials(final String userId, final String password, final PasswordEncoder encoder) {
		final UserEntity originalUser = userRepository.findByUserId(userId);
		log.info("id 검색 완료! " + originalUser);

		if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
			return originalUser;
		}
		return null;
	}
}
