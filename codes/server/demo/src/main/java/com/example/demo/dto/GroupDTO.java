package com.example.demo.dto;

import com.example.demo.model.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupDTO {
	private String groupName;
	private String description;
	private int numOfUsers;
	private String leaderId;
	private String lastModifiedAt;
	private String createdAt;
	private String token;

	public GroupDTO(GroupEntity entity) {
		this.groupName = entity.getGroupName();
		this.description = entity.getDescription();;
		this.numOfUsers = entity.getNumOfUsers();;
		this.leaderId = entity.getLeaderId();;
		this.lastModifiedAt = entity.getLastModifiedAt();;
		this.createdAt = entity.getCreatedAt();;
		this.token = entity.getToken();;
	}

	// DTO -> Entity 변환
	public static GroupEntity toEntity(final GroupDTO dto) {
		return GroupEntity.builder()
				.groupName(dto.getGroupName())
				.description(dto.getDescription())
				.numOfUsers(dto.getNumOfUsers())
				.leaderId(dto.getLeaderId())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.token(dto.getToken())
				.build();
	}
}


