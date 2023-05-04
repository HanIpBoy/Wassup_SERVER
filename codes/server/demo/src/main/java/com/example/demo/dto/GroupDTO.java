package com.example.demo.dto;

import com.example.demo.model.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupDTO {
	private String originKey;
	private String groupName;
	private String description;
	private int numOfUsers;
	private String leaderId;
	private LocalDateTime lastModifiedAt;
	private LocalDateTime createdAt;
	private String groupUsers;
//	private String token;

	public GroupDTO(GroupEntity entity) {
		this.originKey = entity.getOriginKey();
		this.groupName = entity.getGroupName();
		this.description = entity.getDescription();;
		this.numOfUsers = entity.getNumOfUsers();;
		this.leaderId = entity.getLeaderId();;
		this.lastModifiedAt = entity.getLastModifiedAt();;
		this.createdAt = entity.getCreatedAt();;
		this.groupUsers = entity.getGroupUsers();
//		this.token = entity.getToken();;
	}

	// DTO -> Entity 변환
	public static GroupEntity toEntity(final GroupDTO dto) {
		return GroupEntity.builder()
				.originKey(dto.getOriginKey())
				.groupName(dto.getGroupName())
				.description(dto.getDescription())
				.numOfUsers(dto.getNumOfUsers())
				.leaderId(dto.getLeaderId())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.groupUsers(dto.getGroupUsers())
				.build();
	}
}


