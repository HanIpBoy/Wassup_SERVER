package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "groupSchedule")
public class GroupScheduleEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    private String originKey;
    private String groupOriginKey;

    private String name;
    private String startAt;
    private String endAt;
    private String memo;
    private String notification;
    private String allDayToggle;
    private String color;

    @UpdateTimestamp
    private LocalDateTime lastModifiedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
