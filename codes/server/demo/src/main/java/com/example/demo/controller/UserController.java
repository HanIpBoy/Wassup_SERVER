package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;

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

	@PostMapping("/email-verify")
	public ResponseEntity<?> sendEmail(@RequestBody UserDTO userDTO){
		mailService.send(userDTO);
		return ResponseEntity.ok().body(userDTO.getUserId());
	}
	@GetMapping("/email-verify")
	public ResponseEntity<?> emailVerficate(@RequestBody UserDTO userDTO){
		JavaMailSender javaMailSender;
		return ResponseEntity.ok().body(userDTO.getUserId());
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
		try {
			if(userDTO == null || userDTO.getPassword() == null) {
				throw new RuntimeException("Invalid Password value");
			}
			
			UserEntity user = UserEntity.builder()
					.userId(userDTO.getUserId())
					.password(passwordEncoder.encode(userDTO.getPassword()))
					.userName(userDTO.getUserName())
					.birth(userDTO.getBirth())
					.lastModifiedAt(userDTO.getLastModifiedAt())
					.createdAt(userDTO.getCreatedAt())
					.build();
			
			userService.create(user);
			ResponseDTO responseUserDTO = ResponseDTO.builder()
					.success("true")
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
