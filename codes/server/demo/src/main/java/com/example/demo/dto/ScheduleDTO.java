package com.example.demo.dto;

import com.example.demo.model.ScheduleEntity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleDTO {
	private String originKey;
	private String name;
	private String startAt;
	private String endAt;
	private String userId;
	private String memo;
	private String notification;
	private String allDayToggle;
	private LocalDateTime lastModifiedAt;
	private LocalDateTime createdAt;
	private String color;
	private String groupOriginKey;
//	private String token;

	public ScheduleDTO(final ScheduleEntity entity) {
		this.originKey = entity.getOriginKey();
		this.name = entity.getName();
		this.startAt = entity.getStartAt();
		this.endAt = entity.getEndAt();
		this.userId = entity.getUserId();
		this.memo = entity.getMemo();
		this.notification = entity.getNotification();
		this.allDayToggle = entity.getAllDayToggle();
		this.lastModifiedAt = entity.getLastModifiedAt();
		this.createdAt = entity.getCreatedAt();
		this.color = entity.getColor();
		this.groupOriginKey = entity.getGroupOriginKey();
//		this.token = entity.getToken();
	}

	// DTO -> Entity 변환
	public static ScheduleEntity toEntity(final ScheduleDTO dto) {
		return ScheduleEntity.builder()
				.originKey(dto.getOriginKey())
				.name(dto.getName())
				.startAt(dto.getStartAt())
				.endAt(dto.getEndAt())
				.userId(dto.getUserId())
				.memo(dto.getMemo())
				.notification(dto.getNotification())
				.allDayToggle(dto.getAllDayToggle())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.color(dto.getColor())
				.groupOriginKey(dto.getGroupOriginKey())
//				.token(dto.getToken())
				.build();
	}
}


