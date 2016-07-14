package com.geekhub.services.impl;

import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserDocumentRepository;
import com.geekhub.dto.SharedDto;
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

import org.apache.commons.collections.ListUtils;
import org.hibernate.Hibernate;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDocumentServiceImpl implements UserDocumentService {

    @Inject
    private UserDocumentRepository repository;

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

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    @Inject
    private FriendGroupToDocumentRelationService friendGroupToDocumentRelationService;

    @Inject
    private FriendGroupToDirectoryRelationService friendGroupToDirectoryRelationService;

    @Override
    public List<UserDocument> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public UserDocument getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public UserDocument get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(UserDocument entity) {
        return repository.save(entity);
    }

    @Override
    public void update(UserDocument entity) {
        repository.update(entity);
    }

    @Override
    public void delete(UserDocument entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public List<UserDocument> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return repository.getList("owner", owner);
    }

    @Override
    public void moveToTrash(Long docId, Long removerId) {
        UserDocument document = repository.getById(docId);
        RemovedDocument removedDocument = UserFileUtil.wrapUserDocumentInRemoved(document, removerId);
        removedDocumentService.save(removedDocument);
        document.setDocumentStatus(DocumentStatus.REMOVED);
        repository.update(document);
    }

    @Override
    public void moveToTrash(Long[] docIds, Long removerId) {
        Arrays.stream(docIds).forEach(id -> moveToTrash(id, removerId));
    }

    @Override
    public Long recover(Long removedDocId) {
        RemovedDocument removedDocument = removedDocumentService.getById(removedDocId);
        return recover(removedDocument);
    }

    private Long recover(RemovedDocument removedDocument) {
        UserDocument document = removedDocument.getUserDocument();
        document.setDocumentStatus(DocumentStatus.ACTUAL);
        removedDocumentService.delete(removedDocument);
        repository.update(document);
        return document.getId();
    }

    @Override
    public void recover(Long[] removedDocIds) {
        Arrays.stream(removedDocIds).forEach(this::recover);
    }

    @Override
    public UserDocument getByHashName(String hashName) {
        return repository.get("hashName", hashName);
    }

    @Override
    public UserDocument getByNameAndOwnerId(Long ownerId, String name) {
        User owner = userService.getById(ownerId);
        return repository.get(owner, "name", name);
    }

    @Override
    public UserDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name) {
        Map<String, Object> propertiesMap = UserFileUtil.createPropertiesMap(owner, parentDirectoryHash, name);
        return repository.getByFullNameAndOwner(propertiesMap);
    }

    @Override
    public UserDocument getDocumentWithComments(Long docId) {
        UserDocument document = repository.getById(docId);
        Hibernate.initialize(document.getComments());
        return document;
    }

    @Override
    public UserDocument getDocumentWithOldVersions(Long docId) {
        UserDocument document = repository.getById(docId);
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
        UserDocument document = repository.getById(docId);
        Hibernate.initialize(document.getDocumentOldVersions());
        return document;
    }

    @Override
    public List<UserDocument> getAllByParentDirectoryHash(String parentDirectoryHash) {
        return repository.getList("parentDirectoryHash", parentDirectoryHash);
    }

    @Override
    public List<UserDocument> getAllByParentDirectoryHashes(List<String> parentDirectoryHashList) {
        return null;
    }

    @Override
    public List<UserDocument> getAllByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("documentStatus", status);
        return repository.getList(propertiesMap);
    }

    @Override
    public Set<UserDocument> getByIds(List<Long> docIds) {
        return new HashSet<>(repository.getAll("id", docIds));
    }

    @Override
    public Set<User> getAllReadersAndEditors(Long docId) {
        Set<User> users = new HashSet<>();
        users.addAll(userToDocumentRelationService.getAllUsersByDocumentIdBesidesOwner(docId));
        users.addAll(friendGroupToDocumentRelationService.getAllGroupsMembersByDocumentId(docId));
        return users;
    }

    @Override
    public String getLocation(UserDocument document) {
        String location = "";
        String patentDirectoryHash = document.getParentDirectoryHash();
        User owner = userToDocumentRelationService.getDocumentOwner(document);

        while(!patentDirectoryHash.equals(owner.getLogin())) {
            UserDirectory directory = userDirectoryService.getByHashName(patentDirectoryHash);
            location = directory.getName() + "/" + location;
            patentDirectoryHash = directory.getParentDirectoryHash();
        }

        return location;
    }

    @Override
    public Set<UserDocument> getAllByOwnerAndAttribute(User owner, DocumentAttribute attribute) {
        return new HashSet<>(repository.getList(owner, "documentAttribute", attribute));
    }

    @Override
    public void replace(Long docId, String destinationDirectoryHash) {
        UserDocument document = repository.getById(docId);
        UserDirectory destinationDir = null;
//        if (!document.getOwner().getLogin().equals(destinationDirectoryHash)) {
//            destinationDir = userDirectoryService.getByHashName(destinationDirectoryHash);
//            if (destinationDir == null) {
//                return;
//            }
//        }
        String docName = document.getName();

        if (!document.getParentDirectoryHash().equals(destinationDirectoryHash)) {
//            if (getByFullNameAndOwner(document.getOwner(), destinationDirectoryHash, docName) != null) {
//                String docNameWithoutExtension = docName.substring(0, docName.lastIndexOf("."));
//                int matchesCount = repository.getLike(destinationDirectoryHash, docNameWithoutExtension).size();
//                document.setName(docNameWithoutExtension + " (" + (matchesCount + 1) + ")" + document.getExtension());
//            }
            document.setParentDirectoryHash(destinationDirectoryHash);
            if (destinationDir != null) {
                document.setDocumentAttribute(destinationDir.getDocumentAttribute());

//                document.getReaders().clear();
//                document.getReadersGroups().clear();
//
//                destinationDir.getReaders().forEach(document.getReaders()::add);
//                destinationDir.getReadersGroups().forEach(document.getReadersGroups()::add);
            } else {
                document.setDocumentAttribute(DocumentAttribute.PRIVATE);
            }

            repository.update(document);
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
        UserDocument document = repository.getById(docId);
        UserDocument copy = UserFileUtil.copyDocument(document);
        UserDirectory destinationDir = null;

//        if (!document.getOwner().getLogin().equals(destinationDirectoryHash)) {
//            destinationDir = userDirectoryService.getByHashName(destinationDirectoryHash);
//            if (destinationDir == null) {
//                return;
//            }
//        }
        String copyName = document.getName();

//        if (getByFullNameAndOwner(document.getOwner(), destinationDirectoryHash, copyName) != null) {
//            String copyNameWithoutExtension = copyName.substring(0, copyName.lastIndexOf("."));
//            int matchesCount = repository.getLike(destinationDirectoryHash, copyNameWithoutExtension).size();
//            copy.setName(copyNameWithoutExtension + " (" + (matchesCount + 1) + ")" + document.getExtension());
//        }
        copy.setParentDirectoryHash(destinationDirectoryHash);
        copy.setHashName(UserFileUtil.createHashName());

        if (destinationDir != null) {
            copy.setDocumentAttribute(destinationDir.getDocumentAttribute());

//            destinationDir.getReaders().forEach(copy.getReaders()::add);
//            destinationDir.getReadersGroups().forEach(copy.getReadersGroups()::add);
        } else {
            copy.setDocumentAttribute(DocumentAttribute.PRIVATE);
        }

        UserFileUtil.copyFile(document.getHashName(), copy.getHashName());
        repository.save(copy);
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
        Arrays.stream(names).forEach(n -> documents.addAll(repository.search(owner, "name", n)));
        return documents;
    }

    @Override
    public UserDocument saveOrUpdateDocument(MultipartFile multipartFile, UserDirectory parentDirectory,
                                             String description, User user) throws IOException {

        String docName = multipartFile.getOriginalFilename();
        String parentDirectoryHash = parentDirectory == null ? user.getLogin() : parentDirectory.getHashName();
        UserDocument document = getByFullNameAndOwner(user, parentDirectoryHash, docName);

        if (document == null) {
            document = createDocument(multipartFile, parentDirectory, description, user);
        } else if (document.getDocumentStatus() == DocumentStatus.REMOVED) {
            document = recoverAndUpdate(multipartFile, description, user, document);
        } else if (userDocumentAccessService.canEdit(document, user)) {
            document = getDocumentWithOldVersions(document.getId());
            updateDocument(document, user, description, multipartFile);
        }

        return document;
    }

    private UserDocument createDocument(MultipartFile multipartFile, UserDirectory parentDirectory, String description, User user)
            throws IOException {

        UserDocument document = UserFileUtil.createUserDocument(multipartFile, parentDirectory, description, user);
        multipartFile.transferTo(UserFileUtil.createFile(document.getHashName()));
        Long docId = save(document);
        document.setId(docId);
        createRelations(document, parentDirectory, user);
        return document;
    }

    private void createRelations(UserDocument document, UserDirectory parentDirectory, User owner) {
        if (parentDirectory != null) {
            List<UserToDirectoryRelation> userRelations =
                    userToDirectoryRelationService.getAllByDirectory(parentDirectory);
            List<FriendGroupToDirectoryRelation> friendGroupRelations =
                    friendGroupToDirectoryRelationService.getAllByDirectory(parentDirectory);

            userRelations.forEach(r -> userToDocumentRelationService
                    .create(document, r.getUser(), r.getFileRelationType()));

            friendGroupRelations.forEach(r -> friendGroupToDocumentRelationService
                    .create(document, r.getFriendsGroup(), r.getFileRelationType()));
        } else {
            userToDocumentRelationService.create(document, owner, FileRelationType.OWNER);
        }
    }

    private UserDocument recoverAndUpdate(MultipartFile multipartFile, String description, User user, UserDocument document)
            throws IOException {

        RemovedDocument removedDocument = removedDocumentService.getByOwnerAndDocument(user, document);
        Long recoveredDocId = recover(removedDocument);
        document = getDocumentWithOldVersions(recoveredDocId);
        updateDocument(document, user, description, multipartFile);
        return document;
    }

    @Override
    public void updateDocument(UserDocument document, User user, String description, MultipartFile multipartFile)
            throws IOException {

        DocumentOldVersion oldVersion = DocumentVersionUtil.createOldVersion(document);
        document.getDocumentOldVersions().add(oldVersion);
        UserDocument updatedDocument = UserFileUtil.updateUserDocument(document, multipartFile, description, user);
        update(updatedDocument);
        eventSendingService.sendUpdateEvent(updatedDocument, user);
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

        document.setDocumentAttribute(DocumentAttribute.getValue(shared.getAccess()));
        createRelations(document, shared);
        update(document);

        sendEvents(document, shared, user, currentReadersAndEditors);
        return document;
    }

    @Override
    public void shareDocuments(List<UserDocument> documents, SharedDto shared) {
        documents.forEach(d -> createRelations(d, shared));
    }

    private void createRelations(UserDocument document, SharedDto shared) {
        userToDocumentRelationService.deleteByDocumentBesidesOwner(document);
        if (!CollectionUtils.isEmpty(shared.getEditors())) {
            List<User> editors = userService.getByIds(shared.getEditors());
            userToDocumentRelationService.create(document, editors, FileRelationType.EDITOR);
        }
        if (!CollectionUtils.isEmpty(shared.getReaders())) {
            List<User> readers = userService.getByIds(shared.getReaders());
            userToDocumentRelationService.create(document, readers, FileRelationType.READER);
        }

        friendGroupToDocumentRelationService.deleteByDocument(document);
        if (!CollectionUtils.isEmpty(shared.getEditorsGroups())) {
            List<FriendsGroup> editorGroups = friendGroupService.getByIds(shared.getEditorsGroups());
            friendGroupToDocumentRelationService.create(document, editorGroups, FileRelationType.EDITOR);
        }
        if (!CollectionUtils.isEmpty(shared.getReadersGroups())) {
            List<FriendsGroup> readerGroups = friendGroupService.getByIds(shared.getReadersGroups());
            friendGroupToDocumentRelationService.create(document, readerGroups, FileRelationType.READER);
        }
    }

    private void sendEvents(UserDocument document, SharedDto shared, User user, Set<User> oldReadersAndEditors) {
        List<User> groupMembers =
                friendGroupService.getAllMembersByGroupIds(ListUtils.union(shared.getEditorsGroups(), shared.getReaders()));
        List<User> currentAndEditorsSet =
                userService.getByIds(ListUtils.union(groupMembers, ListUtils.union(shared.getEditors(), shared.getReaders())));

        Set<User> newReadersAndEditorsSet = currentAndEditorsSet.stream()
                .filter(u -> !oldReadersAndEditors.contains(u))
                .collect(Collectors.toSet());
        eventSendingService
                .sendShareEvent(newReadersAndEditorsSet, FileType.DOCUMENT, document.getName(), document.getId(), user);

        Set<User> removedReadersAndEditorsSet = oldReadersAndEditors.stream()
                .filter(u -> !currentAndEditorsSet.contains(u))
                .collect(Collectors.toSet());
        eventSendingService.sendProhibitAccessEvent(removedReadersAndEditorsSet, FileType.DOCUMENT, document.getName(), user);
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
    public void updateDocumentAttribute(DocumentAttribute attribute, List<Long> documentIds) {
        repository.updateDocumentAttribute(attribute, documentIds);
    }

    @Override
    public List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash) {
        return repository.getPropertiesList("id", "parentDirectoryHash", parentDirectoryHash);
    }
}
