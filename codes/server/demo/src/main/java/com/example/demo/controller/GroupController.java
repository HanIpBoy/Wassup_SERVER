package com.example.demo.controller;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.ResponseDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("group")
public class GroupController {

    @Autowired
    private GroupService service;

//	private TokenProvider tokenProvider;

	@GetMapping
	public ResponseEntity<?> retrieveGroupList(@AuthenticationPrincipal String userId) {
		List<GroupEntity> entites = service.retrieve(userId);

		List<GroupDTO> dtos = entites.stream().map(GroupDTO::new).collect(Collectors.toList());

		ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

    @PostMapping
	public ResponseEntity<?> createGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			// 그룹을 생성하는 유저가 그룹장이 되기 때문
			entity.setLeaderId(userId);

			List<GroupEntity> entities = service.create(entity);

			List<GroupDTO> dtos = entities.stream().map(GroupDTO::new).collect(Collectors.toList());

			ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping
	public ResponseEntity<?> updateGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		GroupEntity entity = GroupDTO.toEntity(dto);
		log.info("update originKey : " + entity.getOriginKey());

		// 그룹의 정보를 수정할 수 있는 건 그룹장 뿐, update 요청을 보낼 수 있는 건 그룹장 뿐임.
		// 그래서 leaderId를 현재 이를 요청한 유저의 id로 설정해도 됨.
		entity.setLeaderId(userId);

		List<GroupEntity> entities = service.update(entity);

		List<GroupDTO> dtos = entities.stream().map(GroupDTO::new).collect(Collectors.toList());

		ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			entity.setLeaderId(userId);

			List<GroupEntity> entities = service.delete(entity);

			List<GroupDTO> dtos = entities.stream().map(GroupDTO::new).collect(Collectors.toList());

			ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}


}
