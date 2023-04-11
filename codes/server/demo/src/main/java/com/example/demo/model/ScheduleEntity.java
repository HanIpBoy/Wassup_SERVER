package com.example.demo.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Sche")
public class ScheduleEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    private String originKey;
    private String name;
    private String startAt;
    private String endAt;
    private String userId;
    private String memo;
    private Boolean notification;
    private Boolean allDayToggle ;

//    @UpdateTimestamp
    private String lastModifiedAt;

//    @CreationTimestamp
    private String createdAt;
    private String token;
}
