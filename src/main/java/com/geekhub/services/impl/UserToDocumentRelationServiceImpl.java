package com.geekhub.services.impl;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserToDocumentRelationRepository;
import com.geekhub.services.UserToDocumentRelationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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

    @Override
    public List<UserToDocumentRelation> create(UserDocument document, List<User> users, FileRelationType relationType) {
        return users.stream().map(u -> create(document, u, relationType)).collect(Collectors.toList());
    }

    @Override
    public UserToDocumentRelation create(UserDocument document, User user, FileRelationType relationType) {
        UserToDocumentRelation relation = new UserToDocumentRelation();
        relation.setDocument(document);
        relation.setUser(user);
        relation.setFileRelationType(relationType);
        save(relation);
        return relation;
    }

    @Override
    public void deleteByDocumentBesidesOwner(UserDocument document) {
        repository.deleteByDocumentBesidesOwner(document);
    }

    @Override
    public List<UserToDocumentRelation> getAllByDocument(UserDocument document) {
        return repository.getList("document", document);
    }
}
