package com.example.demo.dto;

import com.example.demo.model.GroupUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupUserDTO {
	private String originKey;
	private String groupOriginKey;
	private String userId;
	private LocalDateTime lastModifiedAt;
	private LocalDateTime createdAt;

	public GroupUserDTO(GroupUserEntity entity) {
		this.originKey = entity.getOriginKey();
		this.groupOriginKey = entity.getGroupOriginKey();
		this.userId = entity.getUserId();
		this.lastModifiedAt = entity.getLastModifiedAt();
		this.createdAt = entity.getCreatedAt();
	}

	// DTO -> Entity 변환
	public static GroupUserEntity toEntity(final GroupUserDTO dto) {
		return GroupUserEntity.builder()
				.originKey(dto.getOriginKey())
				.groupOriginKey(dto.getGroupOriginKey())
				.userId(dto.getUserId())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.build();
	}
}


