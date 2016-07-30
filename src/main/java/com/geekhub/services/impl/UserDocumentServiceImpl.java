package com.geekhub.services.impl;

import com.geekhub.dto.*;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserDocumentRepository;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.*;

import java.io.IOException;
import java.util.*;

import org.hibernate.Hibernate;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private EventSendingService eventSendingService;

    @Inject
    private DocumentOldVersionService documentOldVersionService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @Inject
    private FriendGroupToDocumentRelationService friendGroupToDocumentRelationService;

    @Inject
    private FileSharedLinkService fileSharedLinkService;

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

    private Long recover(RemovedDocument removedDocument) {
        UserDocument document = removedDocument.getUserDocument();
        document.setDocumentStatus(DocumentStatus.ACTUAL);
        removedDocumentService.delete(removedDocument);
        repository.update(document);
        return document.getId();
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
        return repository.getList("parentDirectoryHash", parentDirectoryHashList);
    }

    @Override
    public List<UserDocument> getAllByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("documentStatus", status);
        return repository.getList(propertiesMap);
    }

    @Override
    public Set<UserDocument> getAllByIds(Collection<Long> docIds) {
        HashSet<UserDocument> documents = new HashSet<>();
        if (!CollectionUtils.isEmpty(docIds)) {
            documents.addAll(repository.getAll("id", docIds));
        }
        return documents;
    }

    @Override
    public Set<UserDocument> getAllByIds(Long[] docIds) {
        if (docIds == null) {
            return new HashSet<>();
        }
        return getAllByIds(Arrays.asList(docIds));
    }

    @Override
    public Set<User> getAllReadersAndEditors(Long docId) {
        Set<User> users = new HashSet<>();
        users.addAll(userToDocumentRelationService.getAllUsersByDocumentIdBesidesOwner(docId));
        users.addAll(friendGroupToDocumentRelationService.getAllGroupMembersByDocumentId(docId));
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
    public void replace(Set<UserDocument> documents, String destinationDirectoryHash, User user) {
        if (CollectionUtils.isEmpty(documents)) {
            return;
        }
        DirectoryWrapper directory = userDirectoryService.createDirectoryWrapper(destinationDirectoryHash, user);
        documents = setDocumentFullNames(destinationDirectoryHash, documents);
        for (UserDocument doc : documents) {
            doc.setDocumentAttribute(directory.getDocumentAttribute());
            update(doc);
            createRelations(doc, directory);
        }
    }

    @Override
    public void copy(Collection<UserDocument> documents, String destinationDirectoryHash, User user) {
        if (CollectionUtils.isEmpty(documents)) {
            return;
        }
        DirectoryWrapper directory = userDirectoryService.createDirectoryWrapper(destinationDirectoryHash, user);
        documents = setDocumentFullNames(directory.getHashName(), documents.stream().collect(Collectors.toSet()));
        for (UserDocument doc : documents) {
            createCopy(user, directory, doc);
        }
    }

    private void createCopy(User user, DirectoryWrapper relations, UserDocument doc) {
        UserDocument copiedDoc = UserFileUtil.copyDocument(doc);
        copiedDoc.setDocumentAttribute(relations.getDocumentAttribute());
        save(copiedDoc);
        createRelations(copiedDoc, relations);
        userToDocumentRelationService.create(copiedDoc, user, FileRelationType.OWN);
        UserFileUtil.copyFile(doc.getHashName(), copiedDoc.getHashName());
    }

    private Set<UserDocument> setDocumentFullNames(String destinationDirectoryHash, Set<UserDocument> documents) {
        List<String> similarDocNames = getSimilarDocumentNamesInDirectory(destinationDirectoryHash, documents);
        documents.stream().filter(doc -> similarDocNames.contains(doc.getName())).forEach(doc -> {
            int documentIndex = UserFileUtil.countFileNameIndex(similarDocNames, doc);
            String newDocName = doc.getNameWithoutExtension() + " (" + documentIndex + ")" + doc.getExtension();
            doc.setName(newDocName);
            similarDocNames.add(newDocName);
        });
        documents.forEach(d -> d.setParentDirectoryHash(destinationDirectoryHash));
        return documents;
    }

    @Override
    public void copy(Collection<UserDocument> documents, DirectoryWrapper destinationDirectory) {
        for (UserDocument doc : documents) {
            UserDocument copiedDoc = UserFileUtil.copyDocument(doc);
            copiedDoc.setDocumentAttribute(destinationDirectory.getDocumentAttribute());
            copiedDoc.setParentDirectoryHash(destinationDirectory.getHashName());
            save(copiedDoc);
            createRelations(copiedDoc, destinationDirectory);
            userToDocumentRelationService.create(copiedDoc, destinationDirectory.getOwner(), FileRelationType.OWN);
            UserFileUtil.copyFile(doc.getHashName(), copiedDoc.getHashName());
        }
    }

    @Override
    public void add(Long documentId, User user) {
        UserDocument document = getById(documentId);
        UserDocument copiedDocument = UserFileUtil.copyDocument(document);
        copiedDocument.setDocumentAttribute(DocumentAttribute.PRIVATE);
        copiedDocument = setDocumentFullName(user.getLogin(), copiedDocument);
        save(copiedDocument);
        userToDocumentRelationService.create(copiedDocument, user, FileRelationType.OWN);
        UserFileUtil.copyFile(document.getHashName(), copiedDocument.getHashName());
    }

    private UserDocument setDocumentFullName(String destinationDirectoryHash, UserDocument document) {
        List<String> similarDocNames = getSimilarDocumentNamesInDirectory(destinationDirectoryHash, document);
        if (similarDocNames.contains(document.getName())) {
            int documentIndex = UserFileUtil.countFileNameIndex(similarDocNames, document);
            String newDocName = document.getNameWithoutExtension() + " (" + documentIndex + ")" + document.getExtension();
            document.setName(newDocName);
        }
        document.setParentDirectoryHash(destinationDirectoryHash);
        return document;
    }

    @Override
    public void createRelations(List<UserDocument> documents, DirectoryWrapper relations) {
        documents.forEach(d -> {
            createRelations(d, relations);
            d.setDocumentAttribute(relations.getDocumentAttribute());
            update(d);
        });
    }

    @Override
    public DocumentWithLinkDto getBySharedLinkHash(String linkHash) {
        FileSharedLink sharedLink = fileSharedLinkService.getByLinkHash(linkHash);
        UserDocument document = getById(sharedLink.getFileId());
        DocumentWithLinkDto documentDto = EntityToDtoConverter.convertWithLink(document);
        documentDto.setLinkHash(linkHash);
        documentDto.setRelationType(sharedLink.getRelationType());
        documentDto.setAbilityToCommentDocument(document.getAbilityToComment());
        return documentDto;
    }

    private void createRelations(UserDocument document, DirectoryWrapper relations) {
        if (relations == null) {
            return;
        }
        deleteRelations(document);
        if (!CollectionUtils.isEmpty(relations.getUserRelations())) {
            relations.getUserRelations().forEach(r -> userToDocumentRelationService
                    .create(document, r.getUser(), r.getFileRelationType()));
        }
        if (!CollectionUtils.isEmpty(relations.getGroupRelations())) {
            relations.getGroupRelations().forEach(r -> friendGroupToDocumentRelationService
                    .create(document, r.getFriendsGroup(), r.getFileRelationType()));
        }
        if (!CollectionUtils.isEmpty(relations.getReaders())) {
            userToDocumentRelationService.create(document, relations.getReaders(), FileRelationType.READ);
        }
        if (!CollectionUtils.isEmpty(relations.getEditors())) {
            userToDocumentRelationService.create(document, relations.getEditors(), FileRelationType.EDIT);
        }
        if (!CollectionUtils.isEmpty(relations.getReaderGroups())) {
            friendGroupToDocumentRelationService.create(document, relations.getReaderGroups(), FileRelationType.READ);
        }
        if (!CollectionUtils.isEmpty(relations.getEditorGroups())) {
            friendGroupToDocumentRelationService.create(document, relations.getEditorGroups(), FileRelationType.READ);
        }
    }

    private void deleteRelations(UserDocument document) {
        if (document.getId() != null) {
            userToDocumentRelationService.deleteAllBesidesOwnerByDocument(document);
            friendGroupToDocumentRelationService.deleteAllByDocument(document);
        }
    }

    @Override
    public Set<UserDocument> searchByName(User owner, String name) {
        String[] names = name.split(" ");
        Set<UserDocument> documents = new TreeSet<>();
        Arrays.stream(names).forEach(n -> documents.addAll(repository.search(owner, "name", n)));
        return documents;
    }

    @Override
    public List<UserDocument> saveOrUpdateDocument(MultipartFile[] files, String parentDirectoryHash, User user)
            throws IOException {

        List<UserDocument> documents = new ArrayList<>();
        DirectoryWrapper directory = userDirectoryService.createDirectoryWrapper(parentDirectoryHash, user);

        for (MultipartFile file : files) {
            String docName = file.getOriginalFilename();
            UserDocument existingDocument = userToDocumentRelationService
                    .getDocumentByFullNameAndOwner(parentDirectoryHash, docName, user);

            if (existingDocument == null) {
                documents.add(createDocument(file, directory, user));
            } else if (existingDocument.getDocumentStatus() == DocumentStatus.REMOVED) {
                documents.add(recoverAndUpdate(file, user, existingDocument));
            } else {
                existingDocument = getDocumentWithOldVersions(existingDocument.getId());
                documents.add(updateDocument(existingDocument, user, file));
            }
        }
        return documents;
    }

    private UserDocument createDocument(MultipartFile multipartFile, DirectoryWrapper relations, User user)
            throws IOException {

        UserDocument document = UserFileUtil.createUserDocument(multipartFile, relations.getDirectory(), user);
        multipartFile.transferTo(UserFileUtil.createFile(document.getHashName()));
        Long docId = save(document);
        document.setId(docId);
        createRelations(document, relations);
        userToDocumentRelationService.create(document, user, FileRelationType.OWN);
        return document;
    }

    private UserDocument recoverAndUpdate(MultipartFile multipartFile, User user, UserDocument document)
            throws IOException {

        RemovedDocument removedDocument = removedDocumentService.getByOwnerAndDocument(user, document);
        Long recoveredDocId = recover(removedDocument);
        document = getDocumentWithOldVersions(recoveredDocId);
        updateDocument(document, user, multipartFile);
        return document;
    }

    @Override
    public UserDocument updateDocument(UserDocument document, User user, MultipartFile multipartFile)
            throws IOException {

        DocumentOldVersion oldVersion = DocumentVersionUtil.createOldVersion(document);
        document.getDocumentOldVersions().add(oldVersion);
        UserDocument updatedDocument = UserFileUtil.updateUserDocument(document, multipartFile, user);
        update(updatedDocument);
        eventSendingService.sendUpdateEvent(updatedDocument, user);
        return updatedDocument;
    }

    @Override
    public void changeAbilityToComment(Long documentId, boolean abilityToComment) {
        AbilityToCommentDocument ability = AbilityToCommentDocument.getAttribute(abilityToComment);
        UserDocument document = getById(documentId);
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

    private void createRelations(UserDocument document, SharedDto shared) {
        userToDocumentRelationService.deleteAllBesidesOwnerByDocument(document);
        List<User> users = userService.getByIds(CollectionTools.unionLists(shared.getReaders(), shared.getEditors()));

        List<User> editors = CollectionTools.filterUserList(users, shared.getEditors());
        userToDocumentRelationService.create(document, editors, FileRelationType.EDIT);

        List<User> readers = CollectionTools.filterUserList(users, shared.getReaders());
        userToDocumentRelationService.create(document, readers, FileRelationType.READ);

        friendGroupToDocumentRelationService.deleteAllByDocument(document);
        List<FriendsGroup> groups = friendGroupService
                .getByIds(CollectionTools.unionLists(shared.getReaderGroups(), shared.getEditorGroups()));

        List<FriendsGroup> editorGroups = CollectionTools.filterGroupList(groups, shared.getEditorGroups());
        friendGroupToDocumentRelationService.create(document, editorGroups, FileRelationType.EDIT);

        List<FriendsGroup> readerGroups = CollectionTools.filterGroupList(groups, shared.getReaderGroups());
        friendGroupToDocumentRelationService.create(document, readerGroups, FileRelationType.READ);

    }

    private void sendEvents(UserDocument document, SharedDto shared, User user, Set<User> oldReadersAndEditors) {
        List<User> currentReadersAndEditorsList = new ArrayList<>();

        List<Long> groupIdList = CollectionTools.unionLists(shared.getEditorGroups(), shared.getReaderGroups());
        currentReadersAndEditorsList.addAll(friendGroupService.getAllMembersByGroupIds(groupIdList));
        List<Long> userIdList = CollectionTools.unionLists(shared.getReaders(), shared.getEditors());
        currentReadersAndEditorsList.addAll(userService.getByIds(userIdList));

        Set<User> newReadersAndEditorsSet = currentReadersAndEditorsList.stream()
                .filter(u -> !oldReadersAndEditors.contains(u))
                .collect(Collectors.toSet());
        eventSendingService
                .sendShareEvent(newReadersAndEditorsSet, FileType.DOCUMENT, document.getName(), document.getId(), user);

        Set<User> removedReadersAndEditorsSet = oldReadersAndEditors.stream()
                .filter(u -> !currentReadersAndEditorsList.contains(u))
                .collect(Collectors.toSet());
        eventSendingService.sendProhibitAccessEvent(removedReadersAndEditorsSet, FileType.DOCUMENT, document.getName(), user);
    }

    @Override
    public UserDocument recoverOldVersion(DocumentOldVersion oldVersion) {
        UserDocument oldVersionDocument = oldVersion.getUserDocument();
        DocumentOldVersion currentVersion = DocumentVersionUtil.createOldVersion(oldVersionDocument);
        oldVersionDocument.getDocumentOldVersions().add(currentVersion);
        UserDocument recoveredDocument = DocumentVersionUtil.recoverOldVersion(oldVersion);
        oldVersionDocument.getDocumentOldVersions().remove(oldVersion);
        update(recoveredDocument);
        documentOldVersionService.delete(oldVersion);
        return recoveredDocument;
    }

    @Override
    public void recoverRemovedDocument(Long documentId, User user) {
        RemovedDocument removedDocument = removedDocumentService.getByUserDocumentId(documentId);
        Long docId = recover(removedDocument);
        String docName = getById(docId).getName();
        eventSendingService.sendRecoverEvent(this, FileType.DOCUMENT, docName, docId, user);
    }

    @Override
    public void updateDocumentAttribute(DocumentAttribute attribute, List<Long> documentIds) {
        repository.updateDocumentAttribute(attribute, documentIds);
    }

    @Override
    public FileAccessDto findAllRelations(Long documentId) {
        UserDocument document = getById(documentId);
        List<User> editors = userToDocumentRelationService
                .getAllUsersByDocumentAndRelation(document, FileRelationType.EDIT);
        List<User> readers = userToDocumentRelationService
                .getAllUsersByDocumentAndRelation(document, FileRelationType.READ);

        List<FriendsGroup> editorGroups = friendGroupToDocumentRelationService
                .getAllGroupsByDocumentIdAndRelation(document, FileRelationType.EDIT);
        List<FriendsGroup> readerGroups = friendGroupToDocumentRelationService
                .getAllGroupsByDocumentIdAndRelation(document, FileRelationType.READ);

        FileAccessDto fileDto = new FileAccessDto();
        fileDto.setAttribute(document.getDocumentAttribute());
        fileDto.setReaders(EntityToDtoConverter.convertToBaseUserDtos(readers));
        fileDto.setEditors(EntityToDtoConverter.convertToBaseUserDtos(editors));
        fileDto.setReaderGroups(EntityToDtoConverter.convertToFriendGroupDtos(readerGroups));
        fileDto.setEditorGroups(EntityToDtoConverter.convertToFriendGroupDtos(editorGroups));
        return fileDto;
    }

    @Override
    public boolean isDocumentNameValid(String parentDirectoryHash, String docName, User owner) {
        UserDocument existingDocument = userToDocumentRelationService
                .getDocumentByFullNameAndOwner(parentDirectoryHash, docName, owner);
        return existingDocument == null && UserFileUtil.validateDocumentNameWithoutExtension(docName);
    }

    private List<String> getSimilarDocumentNamesInDirectory(String directoryHash, UserDocument document) {
        List<UserDocument> documents = new ArrayList<>();
        documents.add(document);
        return getSimilarDocumentNamesInDirectory(directoryHash, documents);
    }

    @Override
    public List<String> getSimilarDocumentNamesInDirectory(String directoryHash, Collection<UserDocument> documents) {
        if (CollectionUtils.isEmpty(documents)) {
            return new ArrayList<>();
        }
        List<String> documentNames = documents.stream()
                .map(UserDocument::getNameWithoutExtension)
                .collect(Collectors.toList());
        String pattern = UserFileUtil.createNamesPattern(documentNames);
        return repository.getSimilarDocumentNamesInDirectory(directoryHash, pattern);
    }

    @Override
    public List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash) {
        return repository.getPropertiesList("id", "parentDirectoryHash", parentDirectoryHash);
    }
}
