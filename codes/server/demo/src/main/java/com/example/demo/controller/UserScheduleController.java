package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserScheduleDTO;
import com.example.demo.model.GroupScheduleEntity;
import com.example.demo.model.UserScheduleEntity;
import com.example.demo.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService service;

	@GetMapping
	public ResponseEntity<?> retrieveSchedule(@AuthenticationPrincipal String userId) {
		List<UserScheduleEntity> userEntites = service.retrieveUserSchedule(userId);
		List<GroupScheduleEntity> groupEntities = service.retrieveGroupSchedule(userId);

		List<UserScheduleDTO> dtos = userEntites.stream().map(UserScheduleDTO::new).collect(Collectors.toList());

		ResponseDTO response = ResponseDTO.<UserScheduleDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

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

	@DeleteMapping
	public ResponseEntity<?> deleteSchedule(@AuthenticationPrincipal String userId, @RequestBody UserScheduleDTO dto) {
		try {
			UserScheduleEntity entity = UserScheduleDTO.toEntity(dto);

			entity.setUserId(userId);

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
