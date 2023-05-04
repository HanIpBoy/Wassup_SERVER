package com.example.demo.controller;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupUserEntity;
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

	@GetMapping
	public ResponseEntity<?> retrieveGroup(@AuthenticationPrincipal String userId) {
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

			GroupEntity groupEntity = service.create(entity);

			final GroupDTO responseGroupDTO = GroupDTO.builder()
					.originKey(groupEntity.getOriginKey())
					.groupName(groupEntity.getGroupName())
					.description(groupEntity.getGroupName())
					.numOfUsers(groupEntity.getNumOfUsers())
					.leaderId(userId)
					.lastModifiedAt(groupEntity.getLastModifiedAt())
					.createdAt(groupEntity.getCreatedAt())
					.groupUsers(groupEntity.getGroupUsers())
					.build();

			return ResponseEntity.ok().body(responseGroupDTO);

		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping
	public ResponseEntity<?> updateGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		GroupEntity entity = GroupDTO.toEntity(dto);

		entity.setLeaderId(userId);

		GroupEntity groupEntity = service.update(entity);

		final GroupDTO responseGroupDTO = GroupDTO.builder()
				.originKey(groupEntity.getOriginKey())
				.groupName(groupEntity.getGroupName())
				.description(groupEntity.getGroupName())
				.numOfUsers(groupEntity.getNumOfUsers())
				.leaderId(userId)
				.lastModifiedAt(groupEntity.getLastModifiedAt())
				.createdAt(groupEntity.getCreatedAt())
				.groupUsers(groupEntity.getGroupUsers())
				.build();

		return ResponseEntity.ok().body(responseGroupDTO);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			entity.setLeaderId(userId);

			service.delete(entity);

			ResponseDTO response = ResponseDTO.<GroupDTO>builder().status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}


}
