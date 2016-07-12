package com.geekhub.services.impl;

import com.geekhub.controllers.utils.FileControllersUtil;
import com.geekhub.repositories.UserDocumentRepository;
import com.geekhub.dto.SharedDto;
import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.DocumentVersionUtil;
import com.geekhub.utils.UserFileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.Hibernate;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserDocumentServiceImpl implements UserDocumentService {

    @Inject
    private UserDocumentRepository userDocumentRepository;

    @Inject
    private UserService userService;

    @Inject
    private RemovedDocumentService removedDocumentService;

    @Inject
    private FriendGroupService friendGroupService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserDocumentAccessService userDocumentAccessService;

    @Inject
    private EventSendingService eventSendingService;

    @Inject
    private DocumentOldVersionService documentOldVersionService;

    @Override
    public List<UserDocument> getAll(String orderParameter) {
        return userDocumentRepository.getAll(orderParameter);
    }

    @Override
    public UserDocument getById(Long id) {
        return userDocumentRepository.getById(id);
    }

    @Override
    public UserDocument get(String propertyName, Object value) {
        return userDocumentRepository.get(propertyName, value);
    }

    @Override
    public Long save(UserDocument entity) {
        return userDocumentRepository.save(entity);
    }

    @Override
    public void update(UserDocument entity) {
        userDocumentRepository.update(entity);
    }

    @Override
    public void delete(UserDocument entity) {
        userDocumentRepository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        userDocumentRepository.deleteById(entityId);
    }

    @Override
    public List<UserDocument> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return userDocumentRepository.getList("owner", owner);
    }

    @Override
    public void moveToTrash(Long docId, Long removerId) {
        UserDocument document = userDocumentRepository.getById(docId);
        RemovedDocument removedDocument = UserFileUtil.wrapUserDocumentInRemoved(document, removerId);
        removedDocumentService.save(removedDocument);
        document.setDocumentStatus(DocumentStatus.REMOVED);
        userDocumentRepository.update(document);
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
        userDocumentRepository.update(document);
        return document.getId();
    }

    @Override
    public void recover(Long[] removedDocIds) {
        Arrays.stream(removedDocIds).forEach(this::recover);
    }

    @Override
    public UserDocument getByHashName(String hashName) {
        return userDocumentRepository.get("hashName", hashName);
    }

    @Override
    public UserDocument getByNameAndOwnerId(Long ownerId, String name) {
        User owner = userService.getById(ownerId);
        return userDocumentRepository.get(owner, "name", name);
    }

    @Override
    public UserDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name) {
        Map<String, Object> propertiesMap = UserFileUtil.createPropertiesMap(owner, parentDirectoryHash, name);
        return userDocumentRepository.get(propertiesMap);
    }

    @Override
    public UserDocument getDocumentWithComments(Long docId) {
        UserDocument document = userDocumentRepository.getById(docId);
        Hibernate.initialize(document.getComments());
        return document;
    }

    @Override
    public UserDocument getDocumentWithOldVersions(Long docId) {
        UserDocument document = userDocumentRepository.getById(docId);
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
        UserDocument document = userDocumentRepository.getById(docId);
        Hibernate.initialize(document.getDocumentOldVersions());
        return document;
    }

    @Override
    public List<UserDocument> getByParentDirectoryHash(String parentDirectoryHash) {
        return userDocumentRepository.getList("parentDirectoryHash", parentDirectoryHash);
    }

    @Override
    public List<UserDocument> getByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("documentStatus", status);
        return userDocumentRepository.getList(propertiesMap);
    }

    @Override
    public Set<UserDocument> getByIds(List<Long> docIds) {
        return new HashSet<>(userDocumentRepository.getAll("id", docIds));
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
        documents.addAll(userDocumentRepository.getByReader(reader));
        documents.addAll(userDocumentRepository.getByEditor(reader));

        List<FriendsGroup> groups = friendGroupService.getByFriend(reader);
        groups.forEach(g -> {
            documents.addAll(userDocumentRepository.getByReadersGroup(g));
            documents.addAll(userDocumentRepository.getByEditorsGroup(g));
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
        return new HashSet<>(userDocumentRepository.getList(owner, "documentAttribute", attribute));
    }

    @Override
    public Integer getCountByFriendsGroup(FriendsGroup friendsGroup) {
        Set<UserDocument> documents = new HashSet<>();
        documents.addAll(userDocumentRepository.getByEditorsGroup(friendsGroup));
        documents.addAll(userDocumentRepository.getByReadersGroup(friendsGroup));
        return documents.size();
    }

    @Override
    public List<UserDocument> getAllByOwner(User owner) {
        return userDocumentRepository.getList("owner", owner);
    }

    @Override
    public void replace(Long docId, String destinationDirectoryHash) {
        UserDocument document = userDocumentRepository.getById(docId);
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
                int matchesCount = userDocumentRepository.getLike(destinationDirectoryHash, docNameWithoutExtension).size();
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

            userDocumentRepository.update(document);
        }
    }

    @Override
    public boolean replace(Long[] docIds, String destinationDirectoryHash, User user) {
        Set<UserDocument> documents = getByIds(Arrays.asList(docIds));
        if (userDocumentAccessService.isOwner(documents, user)) {
            Arrays.stream(docIds).forEach(id -> replace(id, destinationDirectoryHash));
            return true;
        }
        return false;
    }

    @Override
    public void copy(Long docId, String destinationDirectoryHash) {
        UserDocument document = userDocumentRepository.getById(docId);
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
            int matchesCount = userDocumentRepository.getLike(destinationDirectoryHash, copyNameWithoutExtension).size();
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
        userDocumentRepository.save(copy);
    }

    @Override
    public boolean copy(Long[] docIds, String destinationDirectoryHash, User user) {
        Set<UserDocument> documents = getByIds(Arrays.asList(docIds));
        if (userDocumentAccessService.isOwner(documents, user)) {
            Arrays.stream(docIds).forEach(id -> copy(id, destinationDirectoryHash));
            return true;
        }
        return false;
    }

    @Override
    public Set<UserDocument> searchByName(User owner, String name) {
        String[] names = name.split(" ");
        Set<UserDocument> documents = new TreeSet<>();
        Arrays.stream(names).forEach(n -> documents.addAll(userDocumentRepository.search(owner, "name", n)));
        return documents;
    }

    @Override
    public UserDocument saveOrUpdateDocument(MultipartFile multipartFile, UserDirectory directory,
                                             String description, User user) throws IOException {

        String docName = multipartFile.getOriginalFilename();
        String parentDirectoryHash = directory == null ? user.getLogin() : directory.getHashName();
        UserDocument document = getByFullNameAndOwner(user, parentDirectoryHash, docName);

        if (document == null) {
            document = UserFileUtil.createUserDocument(multipartFile, directory, description, user);
            multipartFile.transferTo(UserFileUtil.createFile(document.getHashName()));
            save(document);
        } else if (document.getDocumentStatus() == DocumentStatus.REMOVED) {
            RemovedDocument removedDocument = removedDocumentService.getByUserDocument(document);
            Long docId = recover(removedDocument.getId());
            document = getDocumentWithOldVersions(docId);
            updateDocument(document, user, description, multipartFile);
        } else if (userDocumentAccessService.canEdit(document, user)) {
            document = getDocumentWithOldVersions(document.getId());
            updateDocument(document, user, description, multipartFile);
        }

        return document;
    }

    @Override
    public void updateDocument(UserDocument document, User user, String description, MultipartFile multipartFile)
            throws IOException {

        DocumentOldVersion oldVersion = DocumentVersionUtil.createOldVersion(document);
        document.getDocumentOldVersions().add(oldVersion);
        update(UserFileUtil.updateUserDocument(document, multipartFile, description, user));
        eventSendingService.sendUpdateEvent(document, user);
    }

    @Override
    public void changeAbilityToComment(UserDocument document, boolean abilityToComment) {
        AbilityToCommentDocument ability = AbilityToCommentDocument.getAttribute(abilityToComment);
        document.setAbilityToComment(ability);
        update(document);
    }

    @Override
    public UserDocument renameDocument(UserDocument document, String newDocName, User user) {
        String oldDocName = document.getName();
        document.setName(newDocName);
        update(document);
        eventSendingService.sendRenameEvent(getAllReadersAndEditors(document.getId()), FileType.DOCUMENT,
                oldDocName, newDocName, document.getId(), user);
        return document;
    }

    @Override
    public UserDocument shareDocument(UserDocument document, SharedDto shared, User user) {
        Set<User> currentReadersAndEditors = getAllReadersAndEditors(document.getId());
        document.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess().toUpperCase()));
        document.setReaders(FileControllersUtil.createEntitySet(shared.getReaders(), userService));
        document.setEditors(FileControllersUtil.createEntitySet(shared.getEditors(), userService));
        document.setReadersGroups(FileControllersUtil.createEntitySet(shared.getReadersGroups(), friendGroupService));
        document.setEditorsGroups(FileControllersUtil.createEntitySet(shared.getEditorsGroups(), friendGroupService));
        update(document);

        Set<User> newReadersAndEditorsSet = getAllReadersAndEditors(document.getId());
        newReadersAndEditorsSet.removeAll(currentReadersAndEditors);
        eventSendingService
                .sendShareEvent(newReadersAndEditorsSet, FileType.DOCUMENT, document.getName(), document.getId(), user);

        newReadersAndEditorsSet = getAllReadersAndEditors(document.getId());
        currentReadersAndEditors.removeAll(newReadersAndEditorsSet);
        eventSendingService.sendProhibitAccessEvent(currentReadersAndEditors, FileType.DOCUMENT, document.getName(), user);

        return document;
    }

    @Override
    public UserDocument recoverOldVersion(DocumentOldVersion oldVersion) {
        UserDocument oldVersionDocument = oldVersion.getUserDocument();
        DocumentOldVersion currentVersion = DocumentVersionUtil.createOldVersion(oldVersionDocument);
        oldVersionDocument.getDocumentOldVersions().add(currentVersion);
        UserDocument recoveredDocument = DocumentVersionUtil.recoverOldVersion(oldVersion);
        update(recoveredDocument);
        documentOldVersionService.delete(oldVersion);
        return recoveredDocument;
    }

    @Override
    public void recoverRemovedDocument(Long removedDocId, User user) {
        Long docId = recover(removedDocId);
        String docName = getById(docId).getName();
        eventSendingService.sendRecoverEvent(this, FileType.DOCUMENT, docName, docId, user);
    }

    @Override
    public List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash) {
        return userDocumentRepository.getPropertiesList("id", "parentDirectoryHash", parentDirectoryHash);
    }
}
