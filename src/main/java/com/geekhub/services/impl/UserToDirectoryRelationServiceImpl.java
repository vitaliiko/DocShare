package com.geekhub.services.impl;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserToDirectoryRelation;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserToDirectoryRelationRepository;
import com.geekhub.services.UserToDirectoryRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserToDirectoryRelationServiceImpl implements UserToDirectoryRelationService {

    @Inject
    private UserToDirectoryRelationRepository repository;

    @Override
    public List<UserToDirectoryRelation> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public UserToDirectoryRelation getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public UserToDirectoryRelation get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(UserToDirectoryRelation entity) {
        return repository.save(entity);
    }

    @Override
    public void update(UserToDirectoryRelation entity) {
        repository.update(entity);
    }

    @Override
    public void delete(UserToDirectoryRelation entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public List<UserToDirectoryRelation> create(UserDirectory directory, List<User> users, FileRelationType relationType) {
        return users.stream().map(u -> create(directory, u, relationType)).collect(Collectors.toList());
    }

    @Override
    public UserToDirectoryRelation create(UserDirectory directory, User user, FileRelationType relationType) {
        UserToDirectoryRelation relation = new UserToDirectoryRelation();
        relation.setDirectory(directory);
        relation.setUser(user);
        relation.setFileRelationType(relationType);
        save(relation);
        return relation;
    }

    @Override
    public void deleteAllBesidesOwnerByDirectory(UserDirectory directory) {
        repository.deleteAllBesidesOwnerByDirectory(directory);
    }

    @Override
    public List<UserToDirectoryRelation> getAllByDirectory(UserDirectory directory) {
        return repository.getList("directory", directory);
    }

    @Override
    public Set<UserDirectory> getAllAccessibleDirectories(User user) {
        return repository.getAllAccessibleDirectories(user).stream().collect(Collectors.toSet());
    }

    @Override
    public Set<UserToDirectoryRelation> getAllAccessibleInRoot(User user, List<String> directoryHashes) {
        if (CollectionUtils.isEmpty(directoryHashes)) {
            return new HashSet<>();
        }
        return repository.getAllAccessibleInRoot(user, directoryHashes).stream().collect(Collectors.toSet());
    }

    @Override
    public List<User> getAllUsersByDirectoryIdAndRelation(UserDirectory directory, FileRelationType relationType) {
        return repository.getAllUsersByDirectoryIdAndRelation(directory, relationType);
    }

    @Override
    public UserToDirectoryRelation getByDirectoryAndUser(UserDirectory directory, User user) {
        return repository.getByDirectoryAndUser(directory, user);
    }

    @Override
    public List<FileRelationType> getAllRelationsByDirectoriesAndUser(List<UserDirectory> directories, User user) {
        if (CollectionUtils.isEmpty(directories)) {
            return new ArrayList<>();
        }
        return repository.getAllRelationsByDirectoriesAndUser(directories, user);
    }

    @Override
    public Long getDirectoriesCountByOwnerAndDirectoryIds(User owner, Long[] directoryIds) {
        List<Long> idList = Arrays.stream(directoryIds).collect(Collectors.toList());
        return repository.getDirectoriesCountByOwnerAndDirectoryIds(owner, idList);
    }

    @Override
    public User getDirectoryOwner(UserDirectory directory) {
        return repository.getDirectoryOwner(directory);
    }
}
