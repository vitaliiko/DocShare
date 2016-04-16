package com.geekhub.services;

import com.geekhub.dao.UserDocumentDao;
import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.utils.UserFileUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private FriendsGroupService friendsGroupService;

    @Autowired
    private UserDirectoryService userDirectoryService;

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
        return userDocumentDao.save(entity);
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
        document.setDocumentStatus(DocumentStatus.REMOVED);
        userDocumentDao.update(document);
    }

    @Override
    public void moveToTrash(Long[] docIds, Long removerId) {
        Arrays.stream(docIds).forEach(id -> moveToTrash(id, removerId));
    }

    @Override
    public Long recover(Long removedDocId) {
        RemovedDocument removedDocument = removedDocumentService.getById(removedDocId);
        UserDocument document = removedDocument.getUserDocument();
        document.setDocumentStatus(DocumentStatus.ACTUAL);
        removedDocumentService.delete(removedDocument);
        userDocumentDao.update(document);
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
    public Set<DocumentOldVersion> getOldVersions(Long docId) {
        UserDocument document = getById(docId);
        Hibernate.initialize(document.getDocumentOldVersions());
        return document.getDocumentOldVersions();
    }

    @Override
    public UserDocument getWithOldVersions(Long docId) {
        return userDocumentDao.getWithOldVersions(docId);
    }

    @Override
    public List<UserDocument> getActualByParentDirectoryHash(String parentDirectoryHash) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("documentStatus", DocumentStatus.ACTUAL);
        return userDocumentDao.getList(propertiesMap);
    }

    @Override
    public List<UserDocument> getRemovedByParentDirectoryHash(String parentDirectoryHash) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("documentStatus", DocumentStatus.REMOVED);
        return userDocumentDao.getList(propertiesMap);
    }

    @Override
    public Set<UserDocument> getByIds(List<Long> docIds) {
        return new HashSet<>(userDocumentDao.getAll("id", docIds));
    }

    @Override
    public Set<User> getAllReadersAndEditors(Long docId) {
        Set<User> users = new HashSet<>();
        UserDocument document = getById(docId);
        users.addAll(document.getReaders());
        users.addAll(document.getEditors());
        document.getReadersGroups().forEach(g -> users.addAll(g.getFriends()));
        document.getEditorsGroups().forEach(g -> users.addAll(g.getFriends()));
        if (document.getOwner() != null) {
            users.add(document.getOwner());
        }
        return users;
    }

    @Override
    public Set<UserDocument> getAllCanRead(User reader) {
        Set<UserDocument> documents = new HashSet<>();
        documents.addAll(userDocumentDao.getByReader(reader));
        documents.addAll(userDocumentDao.getByEditor(reader));

        List<FriendsGroup> groups = friendsGroupService.getByFriend(reader);
        groups.forEach(g -> {
            documents.addAll(userDocumentDao.getByReadersGroup(g));
            documents.addAll(userDocumentDao.getByEditorsGroup(g));
        });

        return documents;
    }

    @Override
    public String getLocation(UserDocument document) {
        String location = "";
        String patentDirectoryHash = document.getParentDirectoryHash();

        while(!patentDirectoryHash.equals(document.getOwner().getLogin())) {
            UserDirectory directory = userDirectoryService.getByHashName(patentDirectoryHash);
            location = directory.getName() + "/" + location;
            patentDirectoryHash = directory.getParentDirectoryHash();
        }

        return location;
    }

    @Override
    public Set<UserDocument> getAllByOwnerAndAttribute(User owner, DocumentAttribute attribute) {
        return new HashSet<>(userDocumentDao.getList(owner, "documentAttribute", attribute));
    }

    @Override
    public Integer getCountByFriendsGroup(FriendsGroup friendsGroup) {
        Set<UserDocument> documents = new HashSet<>();
        documents.addAll(userDocumentDao.getByEditorsGroup(friendsGroup));
        documents.addAll(userDocumentDao.getByReadersGroup(friendsGroup));
        return documents.size();
    }

    @Override
    public List<UserDocument> getAllByOwner(User owner) {
        return userDocumentDao.getList("owner", owner);
    }

    @Override
    public void replace(Long docId, String destinationDirectoryHash) {
        UserDocument document = userDocumentDao.getById(docId);
        document.setParentDirectoryHash(destinationDirectoryHash);
        userDocumentDao.update(document);
    }

    @Override
    public void replace(Long[] docIds, String destinationDirectoryHash) {
        Arrays.stream(docIds).forEach(id -> replace(id, destinationDirectoryHash));
    }

    @Override
    public void copy(Long docId, String destinationDirectoryHash) {
        UserDocument document = userDocumentDao.getById(docId);
        UserDocument copy = null;
        BeanUtils.copyProperties(document, copy);
        copy.setId(null);
        copy.setParentDirectoryHash(destinationDirectoryHash);

        String newHashName = UserFileUtil.createHashName();
        UserFileUtil.copyFile(document.getHashName(), newHashName);
        copy.setHashName(newHashName);

        userDocumentDao.save(copy);
    }

    @Override
    public void copy(Long[] docIds, String destinationDirectoryHash) {
        Arrays.stream(docIds).forEach(id -> copy(id, destinationDirectoryHash));
    }
}
