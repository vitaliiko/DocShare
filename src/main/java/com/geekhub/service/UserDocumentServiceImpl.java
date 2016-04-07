package com.geekhub.service;

import com.geekhub.dao.UserDocumentDao;
import com.geekhub.entity.DocumentOldVersion;
import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.RemovedDocument;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import com.geekhub.util.UserFileUtil;
import java.util.Map;
import java.util.Set;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserDocumentServiceImpl implements UserDocumentService {

    @Autowired
    private UserDocumentDao userDocumentDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RemovedDocumentService removedDocumentService;

    @Override
    public List<UserDocument> getAll(String orderParameter) {
        return userDocumentDao.getAll(orderParameter);
    }

    @Override
    public UserDocument getById(Long id) {
        return userDocumentDao.getById(id);
    }

    @Override
    public UserDocument get(String propertyName, Object value) {
        return userDocumentDao.get(propertyName, value);
    }

    @Override
    public Long save(UserDocument entity) {
        Long docId = userDocumentDao.save(entity);
        Long ownerId = entity.getOwner().getId();
        entity.setId(docId);
        String hashName = UserFileUtil.createHashName(ownerId, docId);
        entity.setHashName(hashName);
        userDocumentDao.update(entity);
        return docId;
    }

    @Override
    public void update(UserDocument entity) {
        userDocumentDao.update(entity);
    }

    @Override
    public void delete(UserDocument entity) {
        userDocumentDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        userDocumentDao.deleteById(entityId);
    }

    @Override
    public List<UserDocument> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return userDocumentDao.getList("owner", owner);
    }

    @Override
    public void moveToTrash(Long docId, Long removerId) {
        UserDocument document = userDocumentDao.getById(docId);
        RemovedDocument removedDocument = UserFileUtil.wrapUserDocument(document, removerId);
        removedDocumentService.save(removedDocument);
        User owner = document.getOwner();
        owner.getUserDocuments().remove(document);
        userService.save(owner);
    }

    @Override
    public void moveToTrash(Long[] docIds, Long removerId) {
        Arrays.stream(docIds).forEach(id -> moveToTrash(id, removerId));
    }

    @Override
    public Long recover(Long removedDocId) {
        RemovedDocument removedDocument = removedDocumentService.getById(removedDocId);
        UserDocument document = removedDocument.getUserDocument();
        User owner = removedDocument.getOwner();
        owner.getUserDocuments().add(document);
        removedDocumentService.delete(removedDocument);
        return document.getId();
    }

    @Override
    public void recover(Long[] removedDocIds) {
        Arrays.stream(removedDocIds).forEach(this::recover);
    }

    @Override
    public UserDocument getByNameAndOwnerId(Long ownerId, String name) {
        User owner = userService.getById(ownerId);
        return userDocumentDao.get(owner, "name", name);
    }

    @Override
    public UserDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name) {
        Map<String, Object> propertiesMap = UserFileUtil.createPropertiesMap(owner, parentDirectoryHash, name);
        return userDocumentDao.get(propertiesMap);
    }

    @Override
    public UserDocument getDocumentWithComments(Long docId) {
        UserDocument document = userDocumentDao.getById(docId);
        Hibernate.initialize(document.getComments());
        return document;
    }

    @Override
    public UserDocument getDocumentWithOldVersions(Long docId) {
        UserDocument document = userDocumentDao.getById(docId);
        Hibernate.initialize(document.getDocumentOldVersions());
        return document;
    }

    @Override
    public Set<User> getReaders(Long docId) {
        UserDocument document = getById(docId);
        Hibernate.initialize(document.getReaders());
        return document.getReaders();
    }

    @Override
    public Set<FriendsGroup> getReadersGroup(Long docId) {
        UserDocument document = getById(docId);
        Hibernate.initialize(document.getReadersGroups());
        return document.getReadersGroups();
    }

    @Override
    public UserDocument getDocumentWithReaders(Long docId) {
        UserDocument document = getById(docId);
        Hibernate.initialize(document.getReadersGroups());
        Hibernate.initialize(document.getReaders());
        return document;
    }

    @Override
    public Set<DocumentOldVersion> getOldVersions(Long docId) {
        UserDocument document = getById(docId);
        Hibernate.initialize(document.getDocumentOldVersions());
        return document.getDocumentOldVersions();
    }
}
