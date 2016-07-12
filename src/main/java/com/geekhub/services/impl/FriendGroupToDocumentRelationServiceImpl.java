package com.geekhub.services.impl;

import com.geekhub.entities.FriendGroupToDocumentRelation;
import com.geekhub.repositories.FriendGroupToDocumentRelationRepository;
import com.geekhub.services.FriendGroupToDocumentRelationService;

import javax.inject.Inject;
import java.util.List;

public class FriendGroupToDocumentRelationServiceImpl implements FriendGroupToDocumentRelationService {

    @Inject
    private FriendGroupToDocumentRelationRepository repository;

    @Override
    public List<FriendGroupToDocumentRelation> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public FriendGroupToDocumentRelation getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public FriendGroupToDocumentRelation get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(FriendGroupToDocumentRelation entity) {
        return repository.save(entity);
    }

    @Override
    public void update(FriendGroupToDocumentRelation entity) {
        repository.update(entity);
    }

    @Override
    public void delete(FriendGroupToDocumentRelation entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }
}
