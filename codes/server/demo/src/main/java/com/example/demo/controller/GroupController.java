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


	@PostMapping("/create")
	public ResponseEntity<?> createGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			entity.setLeaderId(userId);

			//그룹원 수 세팅
			// 처음 그룹을 생성 시에는 그룹 유저가 그룹장 한명 밖에 없음
			entity.setNumOfUsers(1);

			//Group 생성
			GroupEntity responseGroupEntity = groupService.createGroup(entity);

			//그룹장 GroupUser 테이블 생성
			groupService.createGroupUser(userId, responseGroupEntity);

			//생성하는 그룹 정보를 토대로 각 그룹원들의 알림을 알림 DB에 저장
			notificationService.createGroupInviteNotification(dto.getGroupUsers(), responseGroupEntity.getOriginKey());

			GroupDTO groupDTO = setGroupDTO(responseGroupEntity);

			ResponseDTO<?> response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(groupDTO)).status("succeed").build();

			return ResponseEntity.ok().body(response);
		} catch(Exception e) {
			String error = e.getMessage();
			ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().status("fail").error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	/***
	 * 클라이언트에서 받은 NotificationDTO로 GroupUser와 Group을 업데이트, 알림을 삭제하고, 성공했다는 신호만 담아서 반환
	 * @param dto 클라이언트에서 받은 Notification dto
	 * @return ResponseEntity 데이터를 담지 않고, status에 succeed만 담아서 보냄
	 */
	@PostMapping("/invitation/accept")
	public ResponseEntity<?> acceptGroupInvitation(@AuthenticationPrincipal String userId, @RequestBody NotificationDTO dto) {

		// 받은 데이터로 groupEntity를 찾음
		GroupEntity entity = groupService.retrieveGroupByOriginKey(dto.getGroupOriginKey());

		groupService.createGroupUser(userId, entity); // groupUser 테이블을 생성

		groupService.updateGroupNumOfUsers(entity); //group의 numOfUsers 수정(그룹원 +1)

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

		List<GroupDTO> dtos = entites.stream().map(this::setGroupDTO).collect(Collectors.toList());

		ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}


	@GetMapping("/{groupOriginKey}")
	public ResponseEntity<?> retrieveGroup(@PathVariable("groupOriginKey") String groupOriginKey){
		GroupEntity entity = groupService.retrieveGroupByOriginKey(groupOriginKey);

		GroupDTO dtos = setGroupDTO(entity);

		//groupUsers에 Id 대신 넣을 새로운 List 생성
		List<String> userNames = new ArrayList<>();

		//이 API 만 groupUser의 목록을 Id가 아닌 UserName으로 보냄
		for (String userId: dtos.getGroupUsers()) {
			userNames.add(userService.getByUserId(userId).getUserName());
		}

		//GroupUsers의 내용을 바꿔치기
		dtos.setGroupUsers(userNames);

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
			List<GroupScheduleEntity> groupEntities = scheduleService.retrieveGroupScheduleByUserId(entity.getUserId());

			List<UserScheduleDTO> userScheduleDTO = userEntites.stream().map(UserScheduleDTO::new).collect(Collectors.toList());
			List<GroupScheduleDTO> groupScheduleDTO = groupEntities.stream().map(GroupScheduleDTO::new).collect(Collectors.toList());

			ScheduleDTO schedule = new ScheduleDTO(entity.getUserId(), groupScheduleDTO, userScheduleDTO);
			//반환용 List 에 추가
			dtos.add(schedule);
		}

		ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	/***
	 * 그룹의 originKey를 받아서 그룹에 속한 유저들의 개인 일정들만 반환
	 * @param groupOriginKey 그룹의 originKey
	 * @return ResponseEntity <List<UserScheduleDTO>> GroupUser들의 개인일정들을 담은 ResponseEntity 객체
	 */
	@GetMapping("/{groupOriginKey}/user-schedules")
	public ResponseEntity<?> retrieveGroupUserSchedules(@PathVariable("groupOriginKey") String groupOriginKey) {
		List<GroupUserEntity> groupUserEntities= groupService.retrieveUsersByGroupOriginKey(groupOriginKey);

		//반환용 List 생성
		List<UserScheduleDTO> dtos = new ArrayList<>();

		for (GroupUserEntity entity :groupUserEntities) {
			List<UserScheduleEntity> userEntites = scheduleService.retrieveUserSchedules(entity.getUserId());

			List<UserScheduleDTO> userScheduleDTO = userEntites.stream().map(UserScheduleDTO::new).collect(Collectors.toList());

			//반환용 List 에 추가
			dtos.addAll(userScheduleDTO);
		}

		ResponseDTO response = ResponseDTO.<UserScheduleDTO>builder().data(dtos).status("succeed").build();

		return ResponseEntity.ok().body(response);
	}

	/** GroupDTO의 groupUsers 배열에 id를 넘겨주면, 이를 해당 userName으로 바꿔서 반환.
	 * 	ios에서 그룹 타임테이블 만들 때 해당하는 유저의 색상 매핑할 때 사용하는 용도.
	 **/
	@PostMapping("/search/userName")
	public ResponseEntity<?> retrieveUserIdToUserName(@RequestBody GroupDTO dto) {
		List<String> userNameList = new ArrayList<>(); // 반환할 userName들을 담은 List
		List<String> userList = dto.getGroupUsers(); // dto에서 받은 userId들을 담은 List
		userNameList = userService.getByUserIdToUserName(userList);

		GroupDTO responseDTO = GroupDTO.builder().groupUsers(userNameList).build();

		ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(responseDTO)).status("succeed").build();

		return ResponseEntity.ok().body(response); 
	}

	@PutMapping
	public ResponseEntity<?> updateGroup(@AuthenticationPrincipal String userId, @RequestBody GroupDTO dto) {
		try {
			GroupEntity entity = GroupDTO.toEntity(dto);

			//그룹장 검사, 그룹장이 맞으면 GroupEntity의 LedearId를 세팅해줌
			entity = groupService.validateLeader(userId, entity);

			// 그룹장이 확인되면 다시 dto의 내용 세팅
			// (왜 이렇게 하나요?: 그룹장 변경 시 권한 검사 후 그룹장을 변경해야댐)
			entity.setLeaderId(dto.getLeaderId());

			GroupEntity groupEntity = groupService.updateGroup(entity);

			//GroupUser를 변경하고 싶을때
			// GroupUser 생성 시 말고 Update 할 때 추가,삭제 하면 Emitter?
            if(dto.getGroupUsers()!=null)
				groupService.updateGroupUser(groupEntity, dto.getGroupUsers()); // groupUser를 변경해줌

			GroupDTO responseGroupDTO = setGroupDTO(groupEntity);

			ResponseDTO<GroupDTO> response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(responseGroupDTO)).status("succeed").build();

			return ResponseEntity.ok().body(response);
		}catch (Exception e){
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<GroupDTO>builder().status("fail").error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@DeleteMapping("/{originKey}")
	public ResponseEntity<?> deleteGroup(@PathVariable("originKey") String groupOriginKey,@AuthenticationPrincipal String userId) {
		try {

			GroupEntity entity = groupService.retrieveGroupByOriginKey(groupOriginKey);

			//그룹장 검사, 그룹장이 맞으면 GroupEntity의 LedearId를 세팅해줌
			entity = groupService.validateLeader(userId,entity);

			groupService.deleteGroup(entity);

			//groupSchedule 도 삭제해줘야 됨
			scheduleService.deleteGroupScheduels(entity);

			//notiEntity도 삭제해야 함
			for (GroupUserEntity groupUser : groupService.retrieveUsersByGroupOriginKey(entity.getOriginKey())) {
				notificationService.deleteNotificationByUserId(groupUser.getUserId());
			}

			GroupDTO responseGroupDTO = setGroupDTO(entity);

			ResponseDTO response = ResponseDTO.<GroupDTO>builder().data(Collections.singletonList(responseGroupDTO)).status("succeed").build();

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO response = ResponseDTO.<GroupDTO>builder().status("fail").error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	private GroupDTO setGroupDTO(final GroupEntity groupEntity) {
		// 클라에게 보낼 DTO 세팅
		List<GroupUserEntity> groupUserEntities = groupService.retrieveUsersByGroupOriginKey(groupEntity.getOriginKey());
		List<String> userIds = new ArrayList<>();

		// null 체크를 수행하고, null이 아닌 경우에만 userIds에 추가
		for (GroupUserEntity groupUserEntity : groupUserEntities) {
			if (groupUserEntity.getUserId() != null) {
				userIds.add(groupUserEntity.getUserId());
			}
		}

		GroupDTO dtos = new GroupDTO(groupEntity);
		dtos.setGroupUsers(userIds);
		return dtos;
	}

}
