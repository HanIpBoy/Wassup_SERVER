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

			UserScheduleEntity entitiy = service.createUserSchedule(entity);

			UserScheduleDTO dtos = new UserScheduleDTO(entitiy);

			ResponseDTO<UserScheduleDTO> response = ResponseDTO.<UserScheduleDTO>builder().data(Collections.singletonList(dtos)).status("succeed").build();

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

	// 그룹 일정 다 빼고 해당 User의 일정들만 반환
	@GetMapping("/user-schedules")
	public ResponseEntity<?> retrieveUserSchedules(@AuthenticationPrincipal String userId){
		List<UserScheduleEntity> userScheduleEntities = service.retrieveUserSchedules(userId);

		List<UserScheduleDTO> userScheduleDTO = userScheduleEntities.stream().map(UserScheduleDTO::new).collect(Collectors.toList());

		ResponseDTO<UserScheduleDTO> response = ResponseDTO.<UserScheduleDTO>builder().data(userScheduleDTO).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@PutMapping
	public ResponseEntity<?> updateSchedule(@AuthenticationPrincipal String userId, @RequestBody UserScheduleDTO dto) {
		UserScheduleEntity entity = UserScheduleDTO.toEntity(dto);

		entity.setUserId(userId);

		UserScheduleEntity responseEntity = service.updateUserSchedule(entity);

		UserScheduleDTO dtos = new UserScheduleDTO(responseEntity);

		ResponseDTO<UserScheduleDTO> response = ResponseDTO.<UserScheduleDTO>builder().data(Collections.singletonList(dtos)).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping("/{originKey}")
	public ResponseEntity<?> deleteSchedule(@AuthenticationPrincipal String userId, @PathVariable("originKey") String originKey) {
		try {


			UserScheduleEntity entity = service.retrieveUserSchedule(originKey);
			
			//응답용 DTO 미리 세팅
			UserScheduleDTO dtos = new UserScheduleDTO(entity);

			service.deleteUserSchedule(entity);

			ResponseDTO response = ResponseDTO.<UserScheduleDTO>builder().data(Collections.singletonList(dtos)).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<UserScheduleDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
}
