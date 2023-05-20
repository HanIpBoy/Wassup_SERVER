package com.example.demo.dto;

import com.example.demo.model.GroupEntity;
import com.example.demo.model.NotificationEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
public class NotificationDTO {
	private NotificationEntity notification;

	private String groupOriginKey;
	private String isAccepted;

	public NotificationDTO(NotificationEntity notification,String groupOriginKey,String isAccepted) {
		this.notification = notification;
		this.groupOriginKey = groupOriginKey;
		this.isAccepted = isAccepted;
	}
}


