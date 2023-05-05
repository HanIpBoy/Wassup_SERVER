package com.example.demo.service;

import com.example.demo.model.GroupEntity;
import com.example.demo.model.GroupUserEntity;
import com.example.demo.persistence.GroupRepository;
import com.example.demo.persistence.GroupUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

		groupRepository.save(entity);

		return entity;
	}

	public void createGroupUser(GroupUserEntity groupUserEntity) {
		groupUserRepository.save(groupUserEntity);
	}

	public List<GroupEntity> retrieveByUserId(final String userId) {
		// 사용자의 Id로 groupUser 테이블을 검색
		List<GroupUserEntity> groupUserEntities = groupUserRepository.findByUserId(userId);
		List<GroupEntity> groupEntities = new ArrayList<>();; // 최종적으로 반환할 List

		for(GroupUserEntity entitiy : groupUserEntities) {
			groupEntities.add(groupRepository.findByOriginKey(entitiy.getGroupOriginKey()));
		}
		return groupEntities;
	}

	public List<GroupUserEntity> retrieveByGroupOriginKey(final String groupOriginKey){
		return groupUserRepository.findByGroupOriginKey(groupOriginKey);
	}

	public GroupEntity updateGroup(final GroupEntity entity) {
		validate(entity);

		validateLeader(entity);

		final Optional<GroupEntity> original = Optional.ofNullable(groupRepository.findByOriginKey(entity.getOriginKey()));


		original.ifPresent(group -> {
			group.setGroupName(entity.getGroupName() != null ? entity.getGroupName() : group.getGroupName());
			group.setDescription(entity.getDescription() != null ? entity.getDescription() : group.getDescription());
			group.setNumOfUsers(group.getNumOfUsers());
			group.setLeaderId(entity.getLeaderId() != null ? entity.getLeaderId() : group.getLeaderId());
			//group.setGroupUsers(entity.getGroupUsers() != null ? entity.getGroupUsers() : group.getGroupUsers());
			groupRepository.save(group);
		});

		return groupRepository.findByOriginKey(entity.getOriginKey());
	}

	public GroupUserEntity updateGroupUser(final GroupUserEntity entity){
		return null;
	}

	public void delete(final GroupEntity entity) {
		validate(entity);

		try {
			List<GroupUserEntity> groupUserEntities = groupUserRepository.findByGroupOriginKey(entity.getOriginKey());
			for(GroupUserEntity k : groupUserEntities) {
				groupUserRepository.delete(k);
			}
			groupRepository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getOriginKey(), e);

			throw new RuntimeException("error deleting entity " + entity.getOriginKey());
		}
	}

	private void validate(final GroupEntity entity) {
		if(entity == null) {
			log.warn("Entity cannot be null");
			throw new RuntimeException("Entity cannot be null");
		}

		if(entity.getLeaderId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}

	}
	private void validateLeader(final GroupEntity entity) {
		if(!entity.getLeaderId().equals(groupRepository.findByOriginKey(entity.getOriginKey()).getLeaderId())){
			log.warn("Unauthorized user is trying to access the group");
			throw new RuntimeException("Unauthorized user is trying to access the group");
		}
	}


}
