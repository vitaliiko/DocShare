package com.geekhub.services.impl;

import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.repositories.UserToDocumentRelationRepository;
import com.geekhub.services.UserToDocumentRelationService;

import javax.inject.Inject;
import java.util.List;

public class UserToDocumentRelationServiceImpl implements UserToDocumentRelationService {

    @Inject
    private UserToDocumentRelationRepository repository;

    @Override
    public List<UserToDocumentRelation> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public UserToDocumentRelation getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public UserToDocumentRelation get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(UserToDocumentRelation entity) {
        return repository.save(entity);
    }

    @Override
    public void update(UserToDocumentRelation entity) {
        repository.update(entity);
    }

    @Override
    public void delete(UserToDocumentRelation entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }
}
