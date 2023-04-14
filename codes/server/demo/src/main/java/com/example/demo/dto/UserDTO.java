package com.example.demo.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
	private String userId;
	private String password;
	private String userName;
	private String birth;
	private LocalDateTime lastModifiedAt;
	private LocalDateTime createdAt;
	private String token;
	private String emailAuthCode;
}
