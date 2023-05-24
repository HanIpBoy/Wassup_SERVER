package com.example.demo.service;

import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupUserEntity;
import com.example.demo.persistence.GroupRepository;
import com.example.demo.persistence.GroupUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private GroupUserRepository groupUserRepository;

	public GroupEntity createGroup(final GroupEntity entity) {
		// Validations
		validate(entity);

		GroupEntity savedGroupEntity = groupRepository.save(entity);

		return savedGroupEntity;
	}

	public GroupUserEntity createGroupUser(String userId, GroupEntity groupEntity) {
		GroupUserEntity groupUserEntity = null;
		groupUserEntity.setUserId(userId);
		groupUserEntity.setGroupOriginKey(groupEntity.getOriginKey());
		groupUserRepository.save(groupUserEntity);
		return groupUserEntity;
	}

	public List<GroupEntity> retrieveGroupsByUserId(final String userId) {
		// 사용자의 Id로 groupUser 테이블을 검색
		List<GroupUserEntity> groupUserEntities = groupUserRepository.findByUserId(userId);
		
		// 최종적으로 반환할 List 미리 생성
		List<GroupEntity> groupEntities = new ArrayList<>();

		for (GroupUserEntity entitiy : groupUserEntities) {
			groupEntities.add(groupRepository.findByOriginKey(entitiy.getGroupOriginKey()));
		}
		return groupEntities;
	}

	public List<GroupUserEntity> retrieveUsersByGroupOriginKey(final String groupOriginKey) {
		return groupUserRepository.findByGroupOriginKey(groupOriginKey);
	}

	public GroupEntity retrieveGroupByOriginKey(final String groupOriginKey){
		return groupRepository.findByOriginKey(groupOriginKey);
	}

	public GroupEntity updateGroup(final GroupEntity entity) {

		validate(entity);

		final Optional<GroupEntity> original = Optional.ofNullable(groupRepository.findByOriginKey(entity.getOriginKey()));

		original.ifPresent(group -> {
			group.setGroupName(entity.getGroupName() != null ? entity.getGroupName() : group.getGroupName());
			group.setDescription(entity.getDescription() != null ? entity.getDescription() : group.getDescription());
			group.setNumOfUsers(entity.getNumOfUsers());
			group.setLeaderId(entity.getLeaderId() != null ? entity.getLeaderId() : group.getLeaderId());
			groupRepository.save(group);
		});

		return groupRepository.findByOriginKey(entity.getOriginKey());
	}
	public GroupEntity updateGroupNumOfUsers(GroupEntity entity) {
		validate(entity);

		entity.setNumOfUsers(entity.getNumOfUsers()+1);

		return groupRepository.findByOriginKey(entity.getOriginKey());
	}

	public List<GroupUserEntity> updateGroupUser(final GroupEntity groupEntity, final List<String> requestGroupUsers) {
		List<GroupUserEntity> savedGroupUsers = retrieveUsersByGroupOriginKey(groupEntity.getOriginKey());

		// 기존에 저장된 그룹 유저 목록에서 삭제 대상인 유저를 찾아서 삭제
		for (GroupUserEntity savedGroupUser : savedGroupUsers) {
			if (!requestGroupUsers.contains(savedGroupUser.getUserId())) {
				deleteGroupUser(savedGroupUser);
			}
		}

		// 새로 추가할 유저 목록을 찾아서 추가
		for (String uid : requestGroupUsers) {
			boolean isNewUser = true;
			for (Iterator<GroupUserEntity> iterator = savedGroupUsers.iterator(); iterator.hasNext(); ) {
				GroupUserEntity savedGroupUser = iterator.next();
				if (savedGroupUser.getUserId().equals(uid)) {
					isNewUser = false;
					iterator.remove();
					break;
				}
			}
			if (isNewUser) {
				createGroupUser(uid, groupEntity);
			}
		}

		//Group의 NumofUsers 가 바뀌였으면 수정
		if(requestGroupUsers.size()!=groupEntity.getNumOfUsers())
			groupEntity.setNumOfUsers(requestGroupUsers.size());

		return retrieveUsersByGroupOriginKey(groupEntity.getOriginKey());
	}

	public GroupEntity deleteGroup(final GroupEntity entity) {
		validate(entity);

		try {
			List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(entity.getOriginKey());
			for (GroupUserEntity k : groupUserEntities) {
				deleteGroupUser(k);
			}
			groupRepository.delete(entity);
			return null;
		} catch (Exception e) {
			log.error("error deleting entity", entity.getOriginKey(), e);
			throw new RuntimeException("error deleting entity " + entity.getOriginKey());
		}
	}

	public void deleteGroupUser(GroupUserEntity entity) {
		groupUserRepository.delete(entity);
	}

	private void validate(final GroupEntity entity) {
		if (entity == null) {
			log.warn("Entity cannot be null");
			throw new RuntimeException("Entity cannot be null");
		}
		if (entity.getLeaderId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}

	}

	public GroupEntity validateLeader(final String userId, final GroupEntity entity) {
		//Leader가 맞는지 확인
		if (!entity.getLeaderId().equals(userId)) {
			log.warn("Unauthorized user is trying to access the group");
			throw new RuntimeException("Unauthorized user is trying to access the group");
		}
		// Leader가 맞으면 LeaderId로 세팅
		entity.setLeaderId(userId);
		
		return entity;
	}

}

