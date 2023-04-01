package com.example.demo.controller;

import antlr.Token;
import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.ScheduleDTO;
import com.example.demo.model.ScheduleEntity;
import com.example.demo.model.TodoEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService service;

	private TokenProvider tokenProvider;

    @PostMapping
	public ResponseEntity<?> createSchedule(@AuthenticationPrincipal String userId, @RequestBody ScheduleDTO dto) {
		try {
			ScheduleEntity entity = ScheduleDTO.toEntity(dto);

			// dto에서 token 가져온 후에
			String token = dto.getToken();

			// tokenProvider로 토큰 해석해서 userId 알아냄
			tokenProvider.validateAndGetUserId(token);

			entity.setUserId(userId);

			List<ScheduleEntity> entities = service.create(entity);

			List<ScheduleDTO> dtos = entities.stream().map(ScheduleDTO::new).collect(Collectors.toList());

			ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().data(dtos).build();

			return ResponseEntity.ok().body(response);

		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
}
