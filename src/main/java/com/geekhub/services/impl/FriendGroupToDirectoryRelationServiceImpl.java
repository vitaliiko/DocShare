package com.geekhub.services.impl;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.FriendGroupToDirectoryRelationRepository;
import com.geekhub.services.FriendGroupToDirectoryRelationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendGroupToDirectoryRelationServiceImpl implements FriendGroupToDirectoryRelationService {
    
    @Inject
    private FriendGroupToDirectoryRelationRepository repository;

    @Override
    public List<FriendGroupToDirectoryRelation> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public FriendGroupToDirectoryRelation getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public FriendGroupToDirectoryRelation get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(FriendGroupToDirectoryRelation entity) {
        return repository.save(entity);
    }

    @Override
    public void update(FriendGroupToDirectoryRelation entity) {
        repository.update(entity);
    }

    @Override
    public void delete(FriendGroupToDirectoryRelation entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public List<FriendGroupToDirectoryRelation> create(UserDirectory directory, List<FriendsGroup> groups,
                                                       FileRelationType relationType) {

        return groups.stream().map(g -> create(directory, g, relationType)).collect(Collectors.toList());
    }

    @Override
    public FriendGroupToDirectoryRelation create(UserDirectory directory, FriendsGroup group,
                                                 FileRelationType relationType) {

        FriendGroupToDirectoryRelation relation = new FriendGroupToDirectoryRelation();
        relation.setDirectory(directory);
        relation.setFriendsGroup(group);
        relation.setFileRelationType(relationType);
        save(relation);
        return relation;
    }

    @Override
    public void deleteAllByDirectory(UserDirectory directory) {
        repository.deleteAllByDirectory(directory);
    }

    @Override
    public List<FriendGroupToDirectoryRelation> getAllByDirectory(UserDirectory directory) {
        return repository.getList("directory", directory);
    }

    @Override
    public Long getCountByFriendGroup(FriendsGroup group) {
        return repository.getCountByFriendGroup(group);
    }

    @Override
    public List<FriendsGroup> getAllGroupsByDirectoryIdAndRelation(UserDirectory directory, FileRelationType relationType) {
        return repository.getAllGroupsByDirectoryIdAndRelation(directory, relationType);
    }

    @Override
    public List<FileRelationType> getAllRelationsByDirectoryIdAndUser(Long directoryId, User user) {
        return repository.getAllRelationsByDirectoryIdAndUser(directoryId, user);
    }

    @Override
    public Set<UserDirectory> getAllAccessibleDirectories(User user) {
        return repository.getAllAccessibleDirectories(user).stream().collect(Collectors.toSet());
    }
}
