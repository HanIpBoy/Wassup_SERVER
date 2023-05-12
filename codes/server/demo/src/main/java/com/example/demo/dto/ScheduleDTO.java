package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@Builder
public class ScheduleDTO {
    private String userId;
    private List<GroupScheduleDTO> groupSchedules;
    private List<UserScheduleDTO> userSchedules;

    public ScheduleDTO(String userId, List<GroupScheduleDTO> groupSchedules, List<UserScheduleDTO> userSchedules) {
        this.userId = userId;
        this.groupSchedules = groupSchedules;
        this.userSchedules = userSchedules;
    }
}

