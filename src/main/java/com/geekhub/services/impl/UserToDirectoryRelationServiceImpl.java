package com.geekhub.services.impl;

import com.geekhub.entities.UserToDirectoryRelation;
import com.geekhub.repositories.UserToDirectoryRelationRepository;
import com.geekhub.services.UserToDirectoryRelationService;

import javax.inject.Inject;
import java.util.List;

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
}
