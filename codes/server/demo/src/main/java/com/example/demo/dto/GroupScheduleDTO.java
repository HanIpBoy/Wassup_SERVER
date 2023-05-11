package com.example.demo.dto;

import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.PersonalScheduleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupScheduleDTO {
	private String originKey;
	private String groupOriginKey;

	private String name;
	private String startAt;
	private String endAt;
	private String memo;
	private String notification;
	private String allDayToggle;
	private LocalDateTime lastModifiedAt;
	private LocalDateTime createdAt;
	private String color;

	public GroupScheduleDTO(final GroupScheduleEntity entity) {
		this.originKey = entity.getOriginKey();
		this.groupOriginKey = entity.getGroupOriginKey();
		this.name = entity.getName();
		this.startAt = entity.getStartAt();
		this.endAt = entity.getEndAt();
		this.memo = entity.getMemo();
		this.notification = entity.getNotification();
		this.allDayToggle = entity.getAllDayToggle();
		this.lastModifiedAt = entity.getLastModifiedAt();
		this.createdAt = entity.getCreatedAt();
		this.color = entity.getColor();
		this.groupOriginKey = entity.getGroupOriginKey();
	}

	// DTO -> Entity 변환
	public static PersonalScheduleEntity toEntity(final GroupScheduleDTO dto) {
		return PersonalScheduleEntity.builder()
				.originKey(dto.getOriginKey())
				.groupOriginKey(dto.getGroupOriginKey())
				.name(dto.getName())
				.startAt(dto.getStartAt())
				.endAt(dto.getEndAt())
				.memo(dto.getMemo())
				.notification(dto.getNotification())
				.allDayToggle(dto.getAllDayToggle())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.color(dto.getColor())
				.build();
	}
}


