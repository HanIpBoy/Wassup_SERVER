package com.example.demo.dto;

import com.example.demo.model.UserScheduleEntity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserScheduleDTO {
	private String originKey;
	private String name;
	private String startAt;
	private String endAt;
	private String userId;
	private String memo;
	private String allDayToggle;
	private LocalDateTime lastModifiedAt;
	private LocalDateTime createdAt;
	private String color;

	public UserScheduleDTO(final UserScheduleEntity entity) {
		this.originKey = entity.getOriginKey();
		this.name = entity.getName();
		this.startAt = entity.getStartAt();
		this.endAt = entity.getEndAt();
		this.userId = entity.getUserId();
		this.memo = entity.getMemo();
		this.allDayToggle = entity.getAllDayToggle();
		this.lastModifiedAt = entity.getLastModifiedAt();
		this.createdAt = entity.getCreatedAt();
		this.color = entity.getColor();
	}

	// DTO -> Entity 변환
	public static UserScheduleEntity toEntity(final UserScheduleDTO dto) {
		return UserScheduleEntity.builder()
				.originKey(dto.getOriginKey())
				.name(dto.getName())
				.startAt(dto.getStartAt())
				.endAt(dto.getEndAt())
				.userId(dto.getUserId())
				.memo(dto.getMemo())
				.allDayToggle(dto.getAllDayToggle())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.color(dto.getColor())
				.build();
	}
}


