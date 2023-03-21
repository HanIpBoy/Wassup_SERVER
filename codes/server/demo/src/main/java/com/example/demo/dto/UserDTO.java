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
	private String id;
	private String password;
	private String username;
	private String birth;
	private String created_at;
	private String last_modified_at;
	private String token;
}
