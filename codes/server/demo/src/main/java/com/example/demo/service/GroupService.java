package com.example.demo.service;

import com.example.demo.model.GroupEntity;
import com.example.demo.persistence.GroupRepository;
import com.example.demo.persistence.GroupUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private GroupUserRepository groupUserRepository;

	public List<GroupEntity> create(final GroupEntity entity) {
		// Validations
		validate(entity);

		repository.save(entity);

		log.info("Entity id : {} is saved.", entity.getOriginKey());

		return repository.findByOriginKey(entity.getOriginKey());
	}

	public List<GroupEntity> retrieve(final String userId) {
		return repository.findAllByOrderedByStartAtAsc();
	}

	public GroupEntity update(final GroupEntity entity) {
		validate(entity);

		validateLeader(entity);

		final Optional<GroupEntity> original = Optional.ofNullable(groupRepository.findByOriginKey(entity.getOriginKey()));

		original.ifPresent(group -> {
			group.setGroupName(entity.getGroupName() != null ? entity.getGroupName() : group.getGroupName());
			group.setDescription(entity.getDescription() != null ? entity.getDescription() : group.getDescription());
			group.setNumOfUsers(group.getNumOfUsers());
			group.setLeaderId(entity.getLeaderId() != null ? entity.getLeaderId() : group.getLeaderId());
			group.setGroupUsers(entity.getGroupUsers() != null ? entity.getGroupUsers() : group.getGroupUsers());
			groupRepository.save(group);
		});

		return groupRepository.findByOriginKey(entity.getOriginKey());
	}

	public List<GroupEntity> delete(final GroupEntity entity) {
		validate(entity);

		try {
			repository.delete(entity);
		} catch(Exception e) {
			log.error("error deleting entity", entity.getUserId(), e);

			throw new RuntimeException("error deleting entity " + entity.getUserId());
		}

		return retrieve(entity.getUserId());
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
		if(!entity.getLeaderId().equals(groupRepositoryrepository.findByOriginKey(entity.getOriginKey()).getLeaderId())){
			log.warn("Unauthorized user");
			throw new RuntimeException("Unauthorized user");
		}
	}
}
