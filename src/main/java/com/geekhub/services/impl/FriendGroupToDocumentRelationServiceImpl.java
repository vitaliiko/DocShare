package com.geekhub.services.impl;

import com.geekhub.entities.FriendGroupToDocumentRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.FriendGroupToDocumentRelationRepository;
import com.geekhub.services.FriendGroupToDocumentRelationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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

    @Override
    public List<FriendGroupToDocumentRelation> create(UserDocument document, List<FriendsGroup> groups,
                                                      FileRelationType relationType) {

        return groups.stream().map(g -> create(document, g, relationType)).collect(Collectors.toList());
    }

    @Override
    public FriendGroupToDocumentRelation create(UserDocument document, FriendsGroup group, FileRelationType relationType) {
        FriendGroupToDocumentRelation relation = new FriendGroupToDocumentRelation();
        relation.setDocument(document);
        relation.setFriendsGroup(group);
        relation.setFileRelationType(relationType);
        save(relation);
        return relation;
    }

    @Override
    public void deleteByDocument(UserDocument document) {
        repository.deleteByDocument(document);
    }

    @Override
    public List<FriendsGroup> getAllGroupsByDocumentId(Long documentId) {
        return repository.getAllGroupsByDocumentId(documentId);
    }

    @Override
    public List<FriendsGroup> getAllGroupsByDocumentIdAndRelation(UserDocument document, FileRelationType relationType) {
        return repository.getAllGroupsByDocumentIdAndRelation(document, relationType);
    }

    @Override
    public List<User> getAllGroupMembersByDocumentId(Long documentId) {
        return repository.getAllGroupsMembersByDocument(documentId);
    }

    @Override
    public List<FileRelationType> getAllRelationsByDocumentIdAndUser(Long documentId, User user) {
        return repository.getAllRelationsByDocumentIdAndUser(documentId, user);
    }

    @Override
    public List<FriendGroupToDocumentRelation> getAllByDocument(UserDocument document) {
        return repository.getList("document", document);
    }

    @Override
    public Long getCountByFriendGroup(FriendsGroup group) {
        return repository.getCountByFriendGroup(group);
    }
}
