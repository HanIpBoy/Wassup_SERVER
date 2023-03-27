package com.example.demo.dto;

import com.example.demo.model.ScheduleEntity;
import com.example.demo.model.TodoEntity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleDTO {
	private String name;
	private String start;
	private String end;
	private String userId;
	private String memo;
	private Boolean notification;
	private Boolean allDayToggle ;
	private String lastModifiedAt;
	private String createdAt;
	private String token;

	public ScheduleDTO(final ScheduleEntity entity) {
		this.name = entity.getName();
		this.start = entity.getStart();
		this.end = entity.getEnd();
		this.userId = entity.getUserId();
		this.memo = entity.getMemo();
		this.notification = entity.getNotification();
		this.allDayToggle = entity.getAllDayToggle();
		this.lastModifiedAt = entity.getLastModifiedAt();
		this.createdAt = entity.getCreatedAt();
		this.token = entity.getToken();
	}

	// DTO -> Entity 변환
	public static ScheduleEntity toEntity(final ScheduleDTO dto) {
		return ScheduleEntity.builder()
				.name(dto.getName())
				.start(dto.getStart())
				.end(dto.getEnd())
				.userId(dto.getUserId())
				.memo(dto.getMemo())
				.notification(dto.getNotification())
				.allDayToggle(dto.getAllDayToggle())
				.lastModifiedAt(dto.getLastModifiedAt())
				.createdAt(dto.getCreatedAt())
				.token(dto.getToken())
				.build();
	}
}


