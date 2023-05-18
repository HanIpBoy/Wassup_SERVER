package com.example.demo.controller;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserScheduleDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.EmitterService;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private MailService mailService;

	@Autowired
	private EmitterService emitterService;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/***
	 * 유저 전체 리스트를 반환해줌
	 * @return ResponseEntity <List<UserDTO>> 유저 전체 정보를 담은 ResponseEntity 객체
	 */
	@GetMapping("/user")
	public ResponseEntity<?> retrieveUserList() {
		List<UserEntity> entites = userService.retrieve();

		List<UserDTO> dtos = entites.stream().map(UserDTO::new).collect(Collectors.toList());

		ResponseDTO response = ResponseDTO.<UserDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	/***
	 * User 한명을 찾아서 반환해줌
	 * @param userId 찾고 싶은 유저의 userId
	 * @return ResponseEntity <UserDTO> 유저 정보를 담은 ResponseEntity 객체
	 */
	@GetMapping("/user/search/{userId}")
	public ResponseEntity<?> retrieveUser(@PathVariable("userId") String userId) {
		UserEntity userEntity = userService.getByUserId(userId);
		UserDTO responseUserDTO = new UserDTO(userEntity);
		ResponseDTO response = ResponseDTO.<UserDTO>builder().data(Collections.singletonList(responseUserDTO)).status("succeed").build();
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/auth/email-send")
	public ResponseEntity<?> sendEmail(@RequestBody UserDTO userDTO){

		Random random = new Random(System.currentTimeMillis());
		int randomNum = random.nextInt(9000) + 1000;

		userDTO.setEmailAuthCode(String.valueOf(randomNum));

		UserEntity userEntity = UserDTO.toEntity(userDTO);

		UserEntity responseUserEntity = userService.create(userEntity);

		mailService.send(userEntity);

		UserDTO responseUserDTO = new UserDTO(responseUserEntity);

		ResponseDTO<UserDTO> response = ResponseDTO.<UserDTO>builder().data(Collections.singletonList(responseUserDTO)).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/auth/email-verify")
	public ResponseEntity<?> emailVerficate(@RequestBody UserDTO userDTO){

		UserEntity user = userService.getByUserId(userDTO.getUserId());

		if(mailService.verifyEmailCode(user, userDTO.getEmailAuthCode())) {

			UserDTO responseUserDTO = new UserDTO(user);

			ResponseDTO<UserDTO> successResponse = ResponseDTO.<UserDTO>builder().data(Collections.singletonList(responseUserDTO)).status("succeed").build();

			return ResponseEntity.ok().body(successResponse);
		}

		else {
			ResponseDTO<UserDTO> failResponse = ResponseDTO.<UserDTO>builder().status("failure").build();
			return ResponseEntity.ok().body(failResponse);
		}
	}

	@PostMapping("/auth/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
		try {
			if(userService.getByUserId(userDTO.getUserId()) == null) {
				throw new RuntimeException("This User is not email verified");
			}
			if(userDTO == null || userDTO.getPassword() == null) {
				throw new RuntimeException("Invalid Password value");
			}

			UserEntity userEntity = userDTO.toEntity(userDTO);

			userService.update(userEntity);

			ResponseDTO responseUserDTO = ResponseDTO.builder().status("succeed").build();
			
			return ResponseEntity.ok().body(responseUserDTO);
			
		} catch(Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
//	@PostMapping(value = "/auth/signin", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@PostMapping("/auth/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
		UserEntity user = userService.getByCredentials(userDTO.getUserId(), userDTO.getPassword(), passwordEncoder);
		
		if(user != null) {
			final String token = tokenProvider.create(user);
			log.info("token create!");
			log.info(token);
			//이때만 특별히 toEntity()를 사용하지 않고 빌더를 사용해 토큰을 붙여서 보내줌
			final UserDTO responseUserDTO = UserDTO.builder()
					.userId(user.getUserId())
					.userName(user.getUserName())
					.token(token)
					.build();

//			// 유저가 로그인하면, Emitter 만들기
//			SseEmitter emitter = emitterService.subscribe(userDTO.getUserId());
//			try {
//				emitter.send(SseEmitter.event()
//						.name("login")
//						.data(responseUserDTO));
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
			return ResponseEntity.ok().body(responseUserDTO);
		} else {
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error("Login Failed")
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
}

