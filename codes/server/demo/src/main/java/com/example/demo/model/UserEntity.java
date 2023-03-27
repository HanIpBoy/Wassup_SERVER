package com.example.demo.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class UserEntity {
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String originKey;
	
	@NonNull
	private String userId;

	@NonNull
	private String password;

	@NonNull
	private String userName;

	@NonNull
	private String birth;

	@NonNull
	private String lastModifiedAt;

	@NonNull
	private String createdAt;
}
