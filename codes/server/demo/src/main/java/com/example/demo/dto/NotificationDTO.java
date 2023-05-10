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
	private NotificationEntity notification;
	private String isAccepted;

	public NotificationDTO(GroupEntity group, NotificationEntity notification, String isAccepted) {
		this.group = group;
		this.notification = notification;
		this.isAccepted = isAccepted;
	}
}


