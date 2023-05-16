package com.example.demo.controller;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.ResponseDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupUserEntity;
import com.example.demo.model.UserEntity;
import com.example.demo.service.GroupService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<?> retrieveGroup(@AuthenticationPrincipal String userId) {
		List<GroupEntity> entites = groupService.retrieveByUserId(userId);

		List<GroupDTO> dtos = entites.stream().map(GroupDTO::new).collect(Collectors.toList());

		//setGroupDTO()

		ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}



    @PostMapping
	public ResponseEntity<?> createGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			log.info("Server received Group Create request with" + dto);
			GroupEntity entity = GroupDTO.toEntity(dto);

			// 그룹을 생성하는 유저가 그룹장이 되기 때문
			entity.setLeaderId(userId);

			//Group 생성
			GroupEntity groupEntity = groupService.createGroup(entity);

			//GroupUser 생성
			for(String userid : dto.getGroupUsers()){
				UserEntity userEntity = userService.getByUserId(userid);
				//생성하려는 GroupUser가 존재하지 않으면 warn 로그 생성
				if(userEntity == null)
					log.warn(userid + "is not existed");
				//GroupUser 생성
				if(userEntity != null){
					GroupUserEntity groupUserEntity = new GroupUserEntity();
					groupUserEntity.setUserId(userid);
					groupUserEntity.setGroupOriginKey(groupEntity.getOriginKey());
					groupUserEntity.setGroupName(groupEntity.getGroupName());
					groupService.createGroupUser(groupUserEntity);
				}
			}

			List<GroupDTO> dtos = Collections.singletonList(setGroupDTO(groupEntity));
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

		entity.setLeaderId(userId);

		GroupEntity groupEntity = groupService.updateGroup(entity);

		if(dto.getGroupUsers()!=null){
			List<GroupUserEntity> savedGroupUsers =  groupService.retrieveByGroupOriginKey(groupEntity.getOriginKey());
			List<String> requestGroupUsers = dto.getGroupUsers();

			// 기존에 저장된 그룹 유저 목록에서 삭제 대상인 유저를 찾아서 삭제
			for(GroupUserEntity savedGroupUser : savedGroupUsers) {
				if(!requestGroupUsers.contains(savedGroupUser.getUserId())) {
					groupService.deleteGroupUser(savedGroupUser);
				}
			}

			// 새로 추가할 유저 목록을 찾아서 추가
			for(String uid : requestGroupUsers) {
				boolean isNewUser = true;
				for (Iterator<GroupUserEntity> iterator = savedGroupUsers.iterator(); iterator.hasNext();) {
					GroupUserEntity savedGroupUser = iterator.next();
					if (savedGroupUser.getUserId().equals(userId)) {
						isNewUser = false;
						iterator.remove();
						break;
					}
				}

				if(isNewUser) {
					GroupUserEntity newGroupUser = new GroupUserEntity();
					newGroupUser.setGroupOriginKey(groupEntity.getOriginKey());
					newGroupUser.setUserId(uid);
					newGroupUser.setGroupName(groupEntity.getGroupName());
					groupService.createGroupUser(newGroupUser);
				}
			}
		}

		return ResponseEntity.ok().body(setGroupDTO(groupEntity));
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			//권한 검사???

			// 그룹을 생성하는 유저가 그룹장이 되기 때문
			entity.setLeaderId(userId);

			groupService.deleteGroup(entity);

			ResponseDTO response = ResponseDTO.<GroupDTO>builder().status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	private GroupDTO setGroupDTO(GroupEntity groupEntity) {
		// 클라에게 보낼 DTO 세팅
		List<GroupUserEntity> groupUserEntities = groupService.retrieveByGroupOriginKey(groupEntity.getOriginKey());
		List<String> userIds = new ArrayList<>();
		for (GroupUserEntity groupUserEntity : groupUserEntities) {
			userIds.add(groupUserEntity.getUserId());
		}
		GroupDTO dtos = new GroupDTO(groupEntity);
		dtos.setGroupUsers(userIds);
		return dtos;
	}

}
