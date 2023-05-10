package com.example.demo.dto;

import com.example.demo.model.GroupEntity;
import com.example.demo.model.NotificationEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
public class NotificationDTO<T> {
	private GroupEntity group;
	private NotificationEntity noti;

	public NotificationDTO(GroupEntity group, NotificationEntity noti) {
		this.group = group;
		this.noti = noti;
	}
}


