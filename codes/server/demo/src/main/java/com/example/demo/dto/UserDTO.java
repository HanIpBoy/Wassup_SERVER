package com.example.demo.dto;

import com.example.demo.model.UserEntity;
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
	private String token; // user 응답으로 token을 보내줘야 해서 필요
	private String emailAuthCode;

	public UserDTO(final UserEntity entity) {
		this.userId = entity.getUserId();
		this.userName = entity.getUserName();
		this.birth = entity.getBirth();
		this.lastModifiedAt = entity.getLastModifiedAt();
		this.createdAt = entity.getCreatedAt();
	}
	public static UserEntity toEntity(UserDTO userDTO){
		return UserEntity.builder()
				.userId(userDTO.getUserId())
				.password(userDTO.getPassword())
				.userName(userDTO.getUserName())
				.birth(userDTO.getBirth())
				.emailAuthCode(userDTO.getEmailAuthCode())
				.build();
	}
}
