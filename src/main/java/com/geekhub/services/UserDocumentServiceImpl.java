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
import java.util.TreeSet;
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
        RemovedDocument removedDocument = UserFileUtil.wrapUserDocumentInRemoved(document, removerId);
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
        UserDocument document = userDocumentDao.getById(docId);
        Hibernate.initialize(document.getDocumentOldVersions());
        return document;
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
        UserDirectory destinationDir = null;
        if (!document.getOwner().getLogin().equals(destinationDirectoryHash)) {
            destinationDir = userDirectoryService.getByHashName(destinationDirectoryHash);
            if (destinationDir == null) {
                return;
            }
        }
        String docName = document.getName();

        if (!document.getParentDirectoryHash().equals(destinationDirectoryHash)) {
            if (getByFullNameAndOwner(document.getOwner(), destinationDirectoryHash, docName) != null) {
                String docNameWithoutExtension = docName.substring(0, docName.lastIndexOf("."));
                int matchesCount = userDocumentDao.getLike(destinationDirectoryHash, docNameWithoutExtension).size();
                document.setName(docNameWithoutExtension + " (" + (matchesCount + 1) + ")" + document.getExtension());
            }
            document.setParentDirectoryHash(destinationDirectoryHash);
            if (destinationDir != null) {
                document.setDocumentAttribute(destinationDir.getDocumentAttribute());

                document.getReaders().clear();
                document.getReadersGroups().clear();

                destinationDir.getReaders().forEach(document.getReaders()::add);
                destinationDir.getReadersGroups().forEach(document.getReadersGroups()::add);
            } else {
                document.setDocumentAttribute(DocumentAttribute.PRIVATE);
            }

            userDocumentDao.update(document);
        }
    }

    @Override
    public void replace(Long[] docIds, String destinationDirectoryHash) {
        Arrays.stream(docIds).forEach(id -> replace(id, destinationDirectoryHash));
    }

    @Override
    public void copy(Long docId, String destinationDirectoryHash) {
        UserDocument document = userDocumentDao.getById(docId);
        UserDocument copy = UserFileUtil.copyDocument(document);
        UserDirectory destinationDir = null;

        if (!document.getOwner().getLogin().equals(destinationDirectoryHash)) {
            destinationDir = userDirectoryService.getByHashName(destinationDirectoryHash);
            if (destinationDir == null) {
                return;
            }
        }
        String copyName = document.getName();

        if (getByFullNameAndOwner(document.getOwner(), destinationDirectoryHash, copyName) != null) {
            String copyNameWithoutExtension = copyName.substring(0, copyName.lastIndexOf("."));
            int matchesCount = userDocumentDao.getLike(destinationDirectoryHash, copyNameWithoutExtension).size();
            copy.setName(copyNameWithoutExtension + " (" + (matchesCount + 1) + ")" + document.getExtension());
        }
        copy.setParentDirectoryHash(destinationDirectoryHash);
        copy.setHashName(UserFileUtil.createHashName());

        if (destinationDir != null) {
            copy.setDocumentAttribute(destinationDir.getDocumentAttribute());

            destinationDir.getReaders().forEach(copy.getReaders()::add);
            destinationDir.getReadersGroups().forEach(copy.getReadersGroups()::add);
        } else {
            copy.setDocumentAttribute(DocumentAttribute.PRIVATE);
        }

        UserFileUtil.copyFile(document.getHashName(), copy.getHashName());
        userDocumentDao.save(copy);
    }

    @Override
    public void copy(Long[] docIds, String destinationDirectoryHash) {
        Arrays.stream(docIds).forEach(id -> copy(id, destinationDirectoryHash));
    }

    @Override
    public Set<UserDocument> searchByName(User owner, String name) {
        String[] names = name.split(" ");
        Set<UserDocument> documents = new TreeSet<>();
        Arrays.stream(names).forEach(n -> documents.addAll(userDocumentDao.search(owner, "name", n)));
        return documents;
    }

    @Override
    public List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash) {
        return userDocumentDao.getPropertiesList("id", "parentDirectoryHash", parentDirectoryHash);
    }
}
