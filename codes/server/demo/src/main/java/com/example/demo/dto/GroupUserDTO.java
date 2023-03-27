package com.example.demo.dto;

import com.example.demo.model.GroupUserEntity;
import com.example.demo.model.ScheduleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupUserDTO {
	private String userName;
	private String groupName;
	private String userId;
	private String leaderId;
	private String lastModifiedAt;
	private String createdAt;
	private String token;

	public GroupUserDTO(GroupUserEntity entity) {
		this.userName = entity.getUserName();
		this.groupName = entity.getGroupName();
		this.userId = entity.getUserId();
		this.leaderId = entity.getLeaderId();
		this.lastModifiedAt = entity.getLastModifiedAt();
		this.createdAt = entity.getCreatedAt();
		this.token = entity.getToken();
	}

	// DTO -> Entity 변환
	public static GroupUserEntity toEntity(final GroupUserDTO dto) {
		return GroupUserEntity.builder()
				.userName(dto.getUserName())
				.groupName(dto.getGroupName())
				.userId(dto.getUserId())
				.leaderId(dto.getLeaderId())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.token(dto.getToken())
				.build();
	}
}


