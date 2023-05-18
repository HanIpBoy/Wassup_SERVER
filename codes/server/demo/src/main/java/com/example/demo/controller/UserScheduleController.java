package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.UserScheduleEntity;
import com.example.demo.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService service;


    @PostMapping
	public ResponseEntity<?> createSchedule(@AuthenticationPrincipal String userId, @RequestBody UserScheduleDTO dto) {
		try {
			UserScheduleEntity entity = UserScheduleDTO.toEntity(dto);

			entity.setUserId(userId);

			List<UserScheduleEntity> entities = service.createUserSchedule(entity);

			List<UserScheduleDTO> dtos = entities.stream().map(UserScheduleDTO::new).collect(Collectors.toList());

			ResponseDTO<UserScheduleDTO> response = ResponseDTO.<UserScheduleDTO>builder().data(dtos).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<UserScheduleDTO> response = ResponseDTO.<UserScheduleDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	/***
	 * userId를 받아 유저의 개인 일정 + 유저가 속한 모든 그룹 공통 일정 반환
	 * @param userId token에서 얻은 유저의 아이디
	 * @return 유저의 개인 일정 + 유저가 속한 모든 그룹 공통 일정 반환
	 */
	@GetMapping
	public ResponseEntity<?> retrieveSchedule(@AuthenticationPrincipal String userId) {
		List<UserScheduleEntity> userEntites = service.retrieveUserSchedules(userId);
		List<GroupScheduleEntity> groupEntities = service.retrieveGroupSchedule(userId);

		List<UserScheduleDTO> userScheduleDTO = userEntites.stream().map(UserScheduleDTO::new).collect(Collectors.toList());
		List<GroupScheduleDTO> groupScheduleDTO = groupEntities.stream().map(GroupScheduleDTO::new).collect(Collectors.toList());

		ScheduleDTO responseScheduleDTO = new ScheduleDTO(userId, groupScheduleDTO, userScheduleDTO);

		ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().data(Collections.singletonList(responseScheduleDTO)).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@PutMapping
	public ResponseEntity<?> updateSchedule(@AuthenticationPrincipal String userId, @RequestBody UserScheduleDTO dto) {
		UserScheduleEntity entity = UserScheduleDTO.toEntity(dto);
		log.info("update originKey : " + entity.getOriginKey());

		entity.setUserId(userId);

		List<UserScheduleEntity> entities = service.updateUserSchedule(entity);

		List<UserScheduleDTO> dtos = entities.stream().map(UserScheduleDTO::new).collect(Collectors.toList());

		ResponseDTO<UserScheduleDTO> response = ResponseDTO.<UserScheduleDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping("/{originKey}")
	public ResponseEntity<?> deleteSchedule(@AuthenticationPrincipal String userId, @PathVariable("originKey") String originKey) {
		try {
			UserScheduleEntity entity = service.retrieveUserSchedule(originKey);

			List<UserScheduleEntity> entities = service.deleteUserSchedule(entity);

			List<UserScheduleDTO> dtos = entities.stream().map(UserScheduleDTO::new).collect(Collectors.toList());

			ResponseDTO response = ResponseDTO.<UserScheduleDTO>builder().data(dtos).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<UserScheduleDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
}
