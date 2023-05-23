package com.example.demo.dto;

import com.example.demo.model.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationDTO {
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy="uuid")
	private String originKey;
	private String userId;
	private String title;
	private String message;
	private String groupOriginKey;
	private LocalDateTime createdAt;

	public NotificationDTO(final NotificationEntity entity) {
		this.originKey = entity.getOriginKey();
		this.userId = entity.getUserId();
		this.title = entity.getTitle();
		this.message = entity.getMessage();
		this.groupOriginKey = entity.getGroupOriginKey();
		this.createdAt = entity.getCreatedAt();
	}
	public static NotificationEntity toEntity(NotificationDTO notiDTO){
		return NotificationEntity.builder()
				.originKey(notiDTO.getOriginKey())
				.userId(notiDTO.userId)
				.title(notiDTO.getTitle())
				.message(notiDTO.getTitle())
				.groupOriginKey(notiDTO.groupOriginKey)
				.createdAt(notiDTO.createdAt)
				.build();
	}
}



