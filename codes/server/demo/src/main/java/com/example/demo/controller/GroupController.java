package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("group")
public class GroupController {

    @Autowired
    private GroupService groupService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private EmitterService emitterService;

	@PostMapping("/createRequest")
	public ResponseEntity<?> createGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			entity.setLeaderId(userId);

			//그룹원 수 세팅
			entity.setNumOfUsers(dto.getGroupUsers().size());

			//Group 생성
			GroupEntity responseGroupEntity = groupService.createGroup(entity);

			//그룹장 GroupUser 테이블 생성
			GroupUserEntity groupUserEntity = groupService.createGroupUser(userId, entity);

			//생성하는 그룹 정보를 토대로 각 유저 아이디를 세팅해 noti를 만들고 DB에 저장
			List<NotificationDTO> notificationDTO = notificationService.createGroupInviteNotification(dto.getGroupUsers(), entity);

			// 생성한 notificationEntity와 groupEntity로 NotificationDTO를 만들어 SseEmitter로 요청 알림 전송
			emitterService.sendToClients(notificationDTO);

			GroupDTO groupDTO = setGroupDTO(responseGroupEntity);

			ResponseDTO<?> response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(groupDTO)).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/createResponse")
	public ResponseEntity<?> handleGroupCreateResponse(@AuthenticationPrincipal String userId, @RequestBody NotificationDTO dto) {
		// client는 사용자가 요청 알림을 받아서 버튼을 눌렀을 때, 이 API를 사용하면 됨. ResponseDTO의 status에 accept/deny 문자열이 날라옴.

		GroupEntity entity = dto.getGroup();
		if(dto.getIsAccepted().equals("accept")) { // 사용자가 그룹 초대 요청을 수락하면
			groupService.createGroupUser(userId, entity); // groupUser 테이블을 생성

			// 해당 notification을 DB에서 삭제해야 함. -> notificationEntity를 찾아야 함. -> 그래서 매개변수 자체를 notificationDTO로 받음.
			notificationService.deleteNotification(dto.getNotification());
		}

		ResponseDTO response = ResponseDTO.<GroupDTO>builder().status("succeed").build();

		return ResponseEntity.ok().body(response);
	}


	/***
	 *  유저의 아이디를 받아서 유저가 속한 그룹의 리스트를 반환
	 * @param userId 유저의 아이디
	 * @return ResponseEntity <List<GroupDTO>> 유저가 속한 그룹의 리스트를 담은 ResponseEntity 객체
	 */
	@GetMapping
	public ResponseEntity<?> retrieveGroups(@AuthenticationPrincipal String userId) {
		List<GroupEntity> entites = groupService.retrieveGroupsByUserId(userId);

		List<GroupDTO> dtos = entites.stream().map(GroupDTO::new).collect(Collectors.toList());

		//setGroupDTO()

		ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);

	}


	@GetMapping("/{groupOriginKey}")
	public ResponseEntity<?> retrieveGroup(@PathVariable("groupOriginKey") String groupOriginKey){
		GroupEntity entity = groupService.retrieveGroupByOriginKey(groupOriginKey);

		GroupDTO dtos = setGroupDTO(entity);

		ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(dtos)).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}
	/***
	 * 그룹의 OriginKey를 받아서 GroupUser들의 개인일정과 GroupUser들이 속한 그룹의 그룹일정들을 반환
	 * @param groupOriginKey 그룹의 OriginKey
	 * @return ResponseEntity <List<ScheduleDTO>> GroupUser들의 개인일정과 GroupUser들이 속한 그룹의 그룹일정들을 담은 ResponseEntity 객체
	 */
	@GetMapping("/user/schedule/{groupOriginKey}")
	public ResponseEntity<?> retrieveGroupCombinedSchedule(@PathVariable("groupOriginKey") String groupOriginKey){
		
		List<GroupUserEntity> groupUserEntities= groupService.retrieveUsersByGroupOriginKey(groupOriginKey);
		
		//반환용 List 생성
		List<ScheduleDTO> dtos = new ArrayList<>();
		
		for (GroupUserEntity entity :groupUserEntities) {
			List<UserScheduleEntity> userEntites = scheduleService.retrieveUserSchedules(entity.getUserId());
			List<GroupScheduleEntity> groupEntities = scheduleService.retrieveGroupSchedule(entity.getUserId());

			List<UserScheduleDTO> userScheduleDTO = userEntites.stream().map(UserScheduleDTO::new).collect(Collectors.toList());
			List<GroupScheduleDTO> groupScheduleDTO = groupEntities.stream().map(GroupScheduleDTO::new).collect(Collectors.toList());

			ScheduleDTO schedule = new ScheduleDTO(entity.getUserId(), groupScheduleDTO, userScheduleDTO);
			//반환용 List 에 추가
			dtos.add(schedule);
		}

		ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@PutMapping
	public ResponseEntity<?> updateGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		GroupEntity entity = GroupDTO.toEntity(dto);

		//그룹장 검사, 그룹장이 맞으면 GroupEntity의 LedearId를 세팅해줌
		entity = groupService.validateLeader(userId,entity);

		// 그룹장이 확인되면 다시 dto의 내용 세팅
		// (왜 이렇게 하나요?: 그룹장 변경 시 권한 검사 후 그룹장을 변경해야댐)
		entity.setLeaderId(dto.getLeaderId());

		GroupEntity groupEntity = groupService.updateGroup(entity);

		groupService.updateGroupUser(groupEntity,dto.getGroupUsers());

		GroupDTO responseGroupDTO = setGroupDTO(groupEntity);

		ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(responseGroupDTO)).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping("/{originKey}")
	public ResponseEntity<?> deleteGroup(@PathVariable("originKey") String groupOriginKey,@AuthenticationPrincipal String userId) {
		try {

			GroupEntity entity = groupService.retrieveGroupByOriginKey(groupOriginKey);

			//그룹장 검사, 그룹장이 맞으면 GroupEntity의 LedearId를 세팅해줌
			entity = groupService.validateLeader(userId,entity);

			groupService.deleteGroup(entity);

			GroupDTO responseGroupDTO = setGroupDTO(entity);

			ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(responseGroupDTO)).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<GroupDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	private GroupDTO setGroupDTO(GroupEntity groupEntity) {
		// 클라에게 보낼 DTO 세팅
		List<GroupUserEntity> groupUserEntities = groupService.retrieveUsersByGroupOriginKey(groupEntity.getOriginKey());
		List<String> userIds = new ArrayList<>();
		for (GroupUserEntity groupUserEntity : groupUserEntities) {
			userIds.add(groupUserEntity.getUserId());
		}
		GroupDTO dtos = new GroupDTO(groupEntity);
		dtos.setGroupUsers(userIds);
		return dtos;
	}

}
