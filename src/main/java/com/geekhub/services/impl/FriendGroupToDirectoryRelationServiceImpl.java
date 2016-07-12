package com.geekhub.services.impl;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.repositories.FriendGroupToDirectoryRelationRepository;
import com.geekhub.services.FriendGroupToDirectoryRelationService;

import javax.inject.Inject;
import java.util.List;

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
}
