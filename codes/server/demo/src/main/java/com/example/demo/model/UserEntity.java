package com.example.demo.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Usr",uniqueConstraints = {@UniqueConstraint(columnNames = "userId")})
public class UserEntity {
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String originKey;
	
	@NonNull
	private String userId;

	private String password;

	private String userName;

	private String birth;

	@UpdateTimestamp
	private LocalDateTime lastModifiedAt;

	@CreationTimestamp
	private LocalDateTime createdAt;

	private String emailAuthCode;
}
