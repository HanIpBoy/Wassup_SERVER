package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import java.util.random.RandomGenerator;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private MailService mailService;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


	@PostMapping("/email-send")
	public ResponseEntity<?> sendEmail(@RequestBody UserDTO userDTO){

		Random random = new Random(System.currentTimeMillis());
		int randomNum = random.nextInt(9000) + 1000;

		userDTO.setEmailAuthCode(String.valueOf(randomNum));

		UserEntity user = UserEntity.builder()
				.userId(userDTO.getUserId())
				.emailAuthCode(userDTO.getEmailAuthCode())
				.build();

		userService.create(user);

		mailService.send(userDTO);

		return ResponseEntity.ok().body(userDTO.getUserId());
	}

	@PostMapping("/email-verify")
	public ResponseEntity<?> emailVerficate(@RequestBody UserDTO userDTO){

		UserEntity user = userService.getByUserId(userDTO.getUserId());

		if(mailService.verifyEmailCode(user, userDTO.getEmailAuthCode()))
			return ResponseEntity.ok().body(userDTO.getUserId() + " status : success");
		else
			return ResponseEntity.ok().body(userDTO.getUserId() + " status : failure");
	}

	@PostMapping("/signup")
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

			ResponseDTO responseUserDTO = ResponseDTO.builder()
					.status("succeed")
					.build();
			
			return ResponseEntity.ok().body(responseUserDTO);
			
		} catch(Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
		UserEntity user = userService.getByCredentials(userDTO.getUserId(), userDTO.getPassword(), passwordEncoder);
		
		if(user != null) {
			final String token = tokenProvider.create(user);
			log.info("token create!");
			log.info(token);
			final UserDTO responseUserDTO = UserDTO.builder()
					.userId(user.getUserId())
					.token(token)
					.build();

			return ResponseEntity.ok().body(responseUserDTO);
		} else {
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error("Login Failed")
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

}
