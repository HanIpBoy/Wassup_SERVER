package com.example.demo.service;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.UserEntity;
import com.example.demo.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
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
	public List<UserEntity> retrieve() {
		return userRepository.findAll();
	}


	public UserEntity update(UserEntity userEntity) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		final Optional<UserEntity> original = userRepository.findById(userRepository.findByUserId(userEntity.getUserId()).getOriginKey());
		original.ifPresent(user -> {
			user.setUserName(userEntity.getUserName());
			user.setBirth(userEntity.getBirth());
			user.setPassword(passwordEncoder.encode(userEntity.getPassword()));

			userRepository.save(user);
		});
		return userRepository.findByUserId(userEntity.getUserId());
	}


	public UserEntity getByUserId(String userId) {
		try {
			return userRepository.findByUserId(userId);
		}catch (Exception e){
			log.info("UserService의 getByUserId 함수: "+e.getMessage());
			return null;
		}
	}

	public List<String> getByUserIdToUserName(List<String> userList) {
		List<String> userNameList = new ArrayList<>();
		for (int i = 0; i<userList.size(); i++) {
			UserEntity userEntity = userRepository.findByUserId(userList.get(i));
			log.info("UserService의 getByUserIdToUserName 함수: "+ userList.get(i) + " " + userEntity);
			userNameList.add(userEntity.getUserName());
		}
		return userNameList;
	}


	public UserEntity getByOriginKey(String originKey){
		return userRepository.findByOriginKey(originKey);
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
