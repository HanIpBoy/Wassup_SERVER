package com.example.demo.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
	private String userId;
	private String password;
	private String userName;
	private String birth;
	private String createdAt;
	private String lastModifiedAt;
	private String token;
}
