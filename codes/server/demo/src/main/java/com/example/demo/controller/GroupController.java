package com.example.demo.controller;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.NotificationDTO;
import com.example.demo.dto.ResponseDTO;
import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupUserEntity;
import com.example.demo.model.NotificationEntity;
import com.example.demo.model.UserEntity;
import com.example.demo.service.EmitterService;
import com.example.demo.service.GroupService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("group")
public class GroupController {

    @Autowired
    private GroupService groupService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private EmitterService emitterService;

	@GetMapping
	public ResponseEntity<?> retrieveGroup(@AuthenticationPrincipal String userId) {
		List<GroupEntity> entites = groupService.retrieveByUserId(userId);

		List<GroupDTO> dtos = entites.stream().map(GroupDTO::new).collect(Collectors.toList());

		//setGroupDTO()

		ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

    @PostMapping("/createRequest")
	public ResponseEntity<?> createGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			// 그룹을 생성하는 유저가 그룹장이 되기 때문
			entity.setLeaderId(userId);

			//Group 생성
			groupService.createGroup(entity);

			//그룹장 GroupUser 테이블 생성
			GroupUserEntity groupUserEntity = groupService.createGroupUser(userId, entity);

			//생성하는 그룹 정보를 토대로 각 유저 아이디를 세팅해 noti를 만들고 DB에 저장
			List<NotificationEntity> notificationEntities = notificationService.createGroupInviteNotification(dto.getGroupUsers(), entity);

			// 생성한 notificationEntity와 groupEntity로 NotificationDTO를 만들어 SseEmitter로 요청 알림 전송
			emitterService.sendToClients(notificationEntities, entity);

			return ResponseEntity.ok().body("success");

		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/createResponse")
	public ResponseEntity<?> handleGroupCreateResponse(@AuthenticationPrincipal String userId, @RequestBody NotificationDTO dto) {
		// client는 사용자가 요청 알림을 받아서 버튼을 눌렀을 때, 이 API를 사용하면 됨. NotificationDTO의 status에 accept/deny 문자열이 날라옴.

		GroupEntity entity = dto.getGroup();
		if(dto.getIsAccepted().equals("accept")) { // 사용자가 그룹 초대 요청을 수락하면
			groupService.createGroupUser(userId, entity); // groupUser 테이블을 생성

			// 해당 notification을 DB에서 삭제해야 함. -> notificationEntity를 찾아야 함. -> 그래서 매개변수 자체를 notificationDTO로 받음.
			notificationService.deleteNotification(dto.getNotification());
		}
		ResponseDTO response = ResponseDTO.<GroupDTO>builder().status("succeed").build();

		return ResponseEntity.ok().body(response);
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
//					groupService.createGroupUser(newGroupUser);
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
