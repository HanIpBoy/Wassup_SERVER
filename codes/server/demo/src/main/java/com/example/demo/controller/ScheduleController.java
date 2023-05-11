package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.ScheduleDTO;
import com.example.demo.model.ScheduleEntity;
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
public class ScheduleController {

    @Autowired
    private ScheduleService service;

//	private TokenProvider tokenProvider;

	@GetMapping
	public ResponseEntity<?> retrieveSchedule(@AuthenticationPrincipal String userId) {
		List<ScheduleEntity> entites = service.retrieve(userId);

		List<ScheduleDTO> dtos = entites.stream().map(ScheduleDTO::new).collect(Collectors.toList());

		ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

    @PostMapping
	public ResponseEntity<?> createSchedule(@AuthenticationPrincipal String userId, @RequestBody ScheduleDTO dto) {
		try {
			ScheduleEntity entity = ScheduleDTO.toEntity(dto);

			entity.setUserId(userId);

			List<ScheduleEntity> entities = service.create(entity);

			List<ScheduleDTO> dtos = entities.stream().map(ScheduleDTO::new).collect(Collectors.toList());

			ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().data(dtos).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}



	@PutMapping
	public ResponseEntity<?> updateSchedule(@AuthenticationPrincipal String userId, @RequestBody ScheduleDTO dto) {
		ScheduleEntity entity = ScheduleDTO.toEntity(dto);
		log.info("update originKey : " + entity.getOriginKey());

		entity.setUserId(userId);

		List<ScheduleEntity> entities = service.update(entity);

		List<ScheduleDTO> dtos = entities.stream().map(ScheduleDTO::new).collect(Collectors.toList());

		ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteSchedule(@AuthenticationPrincipal String userId, @RequestBody ScheduleDTO dto) {
		try {
			ScheduleEntity entity = ScheduleDTO.toEntity(dto);

			entity.setUserId(userId);

			List<ScheduleEntity> entities = service.delete(entity);

			List<ScheduleDTO> dtos = entities.stream().map(ScheduleDTO::new).collect(Collectors.toList());

			ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().data(dtos).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}


}
