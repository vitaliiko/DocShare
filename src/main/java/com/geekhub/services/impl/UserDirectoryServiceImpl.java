package com.geekhub.services.impl;

import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserDirectoryRepository;
import com.geekhub.dto.SharedDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.UserFileUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional
public class UserDirectoryServiceImpl implements UserDirectoryService {

    @Inject
    private UserDirectoryRepository repository;

    @Inject
    private UserService userService;

    @Inject
    private RemovedDirectoryService removedDirectoryService;

    @Inject
    private RemovedDocumentService removedDocumentService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryAccessService userDirectoryAccessService;

    @Inject
    private EventSendingService eventSendingService;

    @Inject
    private FriendGroupService friendGroupService;

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    @Inject
    private FriendGroupToDirectoryRelationService friendGroupToDirectoryRelationService;

    @Override
    public List<UserDirectory> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public UserDirectory getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public UserDirectory get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(UserDirectory entity) {
        return repository.save(entity);
    }

    @Override
    public void update(UserDirectory entity) {
        repository.update(entity);
    }

    @Override
    public void delete(UserDirectory entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public List<UserDirectory> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return repository.getList("owner", owner);
    }

    @Override
    public void moveToTrash(Long docId, Long removerId) {
        UserDirectory directory = repository.getById(docId);
        RemovedDirectory removedDirectory = UserFileUtil.wrapUserDirectoryInRemoved(directory, removerId);
        removedDirectoryService.save(removedDirectory);

        setRemovedStatus(directory);
        repository.update(directory);
    }

    private void setRemovedStatus(UserDirectory directory) {
        directory.setDocumentStatus(DocumentStatus.REMOVED);

        List<UserDocument> documents = userDocumentService
                .getAllByParentDirectoryHashAndStatus(directory.getHashName(), DocumentStatus.ACTUAL);
        documents.forEach(d -> d.setDocumentStatus(DocumentStatus.REMOVED));

        List<UserDirectory> directories =
                getByParentDirectoryHashAndStatus(directory.getHashName(), DocumentStatus.ACTUAL);
        directories.forEach(this::setRemovedStatus);
    }

    @Override
    public void moveToTrash(Long[] dirIds, Long removerId) {
        Arrays.stream(dirIds).forEach(id -> moveToTrash(id, removerId));
    }

    @Override
    public Long recover(Long removedDirIds) {
        RemovedDirectory removedDocument = removedDirectoryService.getById(removedDirIds);
        UserDirectory directory = removedDocument.getUserDirectory();
        removedDirectoryService.delete(removedDocument);

        setActualStatus(directory);
        repository.update(directory);

//        eventSendingService.sendRecoverEvent(this, FileType.DIRECTORY, directory.getName(), directory.getId(), directory.getOwner());
        return directory.getId();
    }

    private void setActualStatus(UserDirectory directory) {
        directory.setDocumentStatus(DocumentStatus.ACTUAL);

        List<UserDocument> documents = userDocumentService
                .getAllByParentDirectoryHashAndStatus(directory.getHashName(), DocumentStatus.REMOVED);
//        documents.stream()
//                .filter(d -> removedDocumentService.getByOwnerAndDocument(d) == null)
//                .forEach(d -> d.setDocumentStatus(DocumentStatus.ACTUAL));

        List<UserDirectory> directories =
                getByParentDirectoryHashAndStatus(directory.getHashName(), DocumentStatus.REMOVED);
        directories.stream()
                .filter(d -> removedDirectoryService.getByUserDirectory(d) == null)
                .forEach(this::setActualStatus);
    }

    @Override
    public void recover(Long[] dirIds) {
        Arrays.stream(dirIds).forEach(this::recover);
    }

    @Override
    public UserDirectory getByNameAndOwnerId(Long ownerId, String name) {
        User owner = userService.getById(ownerId);
        return repository.get(owner, "name", name);
    }

    @Override
    public UserDirectory getByFullNameAndOwner(User owner, String parentDirectoryHash, String name) {
        Map<String, Object> propertiesMap = UserFileUtil.createPropertiesMap(owner, parentDirectoryHash, name);
        return repository.getByFullNameAndOwner(propertiesMap);
    }

    @Override
    public List<UserDirectory> getAllByParentDirectoryHash(String parentDirectoryHash) {
        return repository.getList("parentDirectoryHash", parentDirectoryHash);
    }

    @Override
    public List<UserDirectory> getTreeByParentDirectoryHash(String parentDirectoryHash) {
        return null;
    }

    @Override
    public List<UserDirectory> getByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("documentStatus", status);
        return repository.getList(propertiesMap);
    }

    @Override
    public UserDirectory getByHashName(String hashName) {
        return repository.get("hashName", hashName);
    }

    @Override
    public UserDirectory createDirectory(User owner, UserDirectory parentDirectory, String dirName) {
        String parentDirHash = parentDirectory == null ? owner.getLogin() : parentDirectory.getHashName();
        UserDirectory directory = getByFullNameAndOwner(owner, parentDirHash, dirName);

        if (directory != null && !UserFileUtil.validateDirectoryName(dirName)) {
            return null;
        }
        directory = UserFileUtil.createUserDirectory(owner, parentDirHash, dirName);
        long dirId = save(directory);
        directory.setId(dirId);
        createRelations(owner, parentDirectory, directory);
        return directory;
    }

    private void createRelations(User owner, UserDirectory parentDirectory, UserDirectory directory) {
        if (parentDirectory != null) {
            List<UserToDirectoryRelation> userRelations =
                    userToDirectoryRelationService.getAllByDirectory(parentDirectory);
            List<FriendGroupToDirectoryRelation> friendGroupRelations =
                    friendGroupToDirectoryRelationService.getAllByDirectory(parentDirectory);

            friendGroupRelations.forEach(r -> friendGroupToDirectoryRelationService
                    .create(directory, r.getFriendsGroup(), r.getFileRelationType()));
            userRelations.forEach(r -> userToDirectoryRelationService
                    .create(directory, r.getUser(), r.getFileRelationType()));
        } else {
            userToDirectoryRelationService.create(directory, owner, FileRelationType.OWNER);
        }
    }

    @Override
    public Set<UserDirectory> getByIds(List<Long> dirIds) {
        return new HashSet<>(repository.getAll("id", dirIds));
    }

    @Override
    public Set<User> getAllReadersAndEditors(Long docId) {
        Set<User> users = new HashSet<>();
//        UserDirectory document = getById(docId);
//        users.addAll(document.getReaders());
//        document.getReadersGroups().forEach(g -> users.addAll(g.getFriends()));
        return users;
    }

    @Override
    public String getLocation(UserDirectory directory) {
        String location = "";
        String patentDirectoryHash = directory.getParentDirectoryHash();

//        while(!patentDirectoryHash.equals(directory.getOwner().getLogin())) {
//            UserDirectory dir = getByHashName(patentDirectoryHash);
//            location = dir.getName() + "/" + location;
//            patentDirectoryHash = dir.getParentDirectoryHash();
//        }

        return location;
    }

    @Override
    public Set<UserDirectory> getActualByOwner(User owner) {
        return new HashSet<>(repository.getList(owner, "documentAttribute", DocumentStatus.ACTUAL));
    }

    @Override
    public void replace(Long dirId, String destinationDirectoryHash) {
        UserDirectory directory = repository.getById(dirId);
        String dirName = directory.getName();
        String dirLocation = getLocation(directory) + dirName;
        String destinationDirLocation = getDestinationDirectoryLocation(directory, destinationDirectoryHash);

        if (!destinationDirLocation.startsWith(dirLocation)) {
//            if (getByFullNameAndOwner(directory.getOwner(), destinationDirectoryHash, dirName) != null) {
//                int matchesCount = repository.getLike(destinationDirectoryHash, dirName).size();
//                directory.setName(dirName + " (" + (matchesCount + 1) + ")");
//            }
            directory.setParentDirectoryHash(destinationDirectoryHash);
            repository.update(directory);
        }
    }

    @Override
    public boolean replace(Long[] dirIds, String destinationDirectoryHash, User user) {
        Set<UserDirectory> directories = getByIds(Arrays.asList(dirIds));
        if (userDirectoryAccessService.isOwner(directories, user)) {
            Arrays.stream(dirIds).forEach(id -> replace(id, destinationDirectoryHash));
            return true;
        }
        return false;
    }

    @Override
    public Set<UserDirectory> searchByName(User owner, String name) {
        String[] names = name.split(" ");
        Set<UserDirectory> directories = new TreeSet<>();
        Arrays.stream(names).forEach(n -> directories.addAll(repository.search(owner, "name", n)));
        return directories;
    }

    @Override
    public UserDirectory renameDirectory(UserDirectory directory, String newDirName, User owner) {
        String oldDirName = directory.getName();
        directory.setName(newDirName);
        update(directory);

        eventSendingService.sendRenameEvent(getAllReadersAndEditors(directory.getId()), FileType.DIRECTORY, oldDirName,
                newDirName, directory.getId(), owner);

        return directory;
    }

    @Override
    public UserDirectory shareDirectory(UserDirectory directory, SharedDto sharedDto, User user) {
        Set<User> currentReadersAndEditors = getAllReadersAndEditors(directory.getId());

        directory.setDocumentAttribute(DocumentAttribute.valueOf(sharedDto.getAccess()));
        update(directory);
        createRelations(directory, sharedDto);
        createRelationsForChilds(directory.getHashName(), sharedDto);

        sendEvents(directory, user, currentReadersAndEditors);
        return directory;
    }

    private void createRelationsForChilds(String dirHashName, SharedDto sharedDto) {
        List<UserDirectory> childDirectories = getTreeByParentDirectoryHash(dirHashName);
        if (!CollectionUtils.isEmpty(childDirectories)) {
            childDirectories.forEach(d -> createRelations(d, sharedDto));

            List<String> parentDirHashList = childDirectories.stream().map(UserDirectory::getHashName).collect(Collectors.toList());
            parentDirHashList.add(dirHashName);
            List<UserDocument> childDocuments = userDocumentService.getAllByParentDirectoryHashes(parentDirHashList);
            userDocumentService.shareDocuments(childDocuments, sharedDto);
        }
    }

    private void createRelations(UserDirectory directory, SharedDto sharedDto) {
        userToDirectoryRelationService.deleteByDirectoryBesidesOwner(directory);
        if (!CollectionUtils.isEmpty(sharedDto.getReaders())) {
            List<User> readers = userService.getByIds(sharedDto.getReaders());
            userToDirectoryRelationService.create(directory, readers, FileRelationType.READER);
        }
        if (!CollectionUtils.isEmpty(sharedDto.getEditors())) {
            List<User> editors = userService.getByIds(sharedDto.getEditors());
            userToDirectoryRelationService.create(directory, editors, FileRelationType.EDITOR);
        }

        friendGroupToDirectoryRelationService.deleteByDirectory(directory);
        if (!CollectionUtils.isEmpty(sharedDto.getReadersGroups())) {
            List<FriendsGroup> readerGroups = friendGroupService.getByIds(sharedDto.getReadersGroups());
            friendGroupToDirectoryRelationService.create(directory, readerGroups, FileRelationType.READER);
        }
        if (!CollectionUtils.isEmpty(sharedDto.getEditorsGroups())) {
            List<FriendsGroup> editorGroups = friendGroupService.getByIds(sharedDto.getReadersGroups());
            friendGroupToDirectoryRelationService.create(directory, editorGroups, FileRelationType.EDITOR);
        }
    }

    private void sendEvents(UserDirectory directory, User user, Set<User> currentReadersAndEditors) {
        Set<User> newReaderSet = getAllReadersAndEditors(directory.getId());
        newReaderSet.removeAll(currentReadersAndEditors);
        eventSendingService.sendShareEvent(newReaderSet, FileType.DIRECTORY, directory.getName(), directory.getId(), user);

        newReaderSet = getAllReadersAndEditors(directory.getId());
        currentReadersAndEditors.removeAll(newReaderSet);
        eventSendingService.sendProhibitAccessEvent(currentReadersAndEditors, FileType.DIRECTORY, directory.getName(), user);
    }

    @Override
    public Set<UserFileDto> getDirectoryContent(String dirHashName) {
        List<UserDocument> documents;
        List<UserDirectory> directories;
        documents = userDocumentService.getAllByParentDirectoryHashAndStatus(dirHashName, DocumentStatus.ACTUAL);
        directories = getByParentDirectoryHashAndStatus(dirHashName, DocumentStatus.ACTUAL);

        Set<UserFileDto> dtoSet = new TreeSet<>();
        if (documents != null) {
            documents.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        if (directories != null) {
            directories.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        return dtoSet;
    }

    @Override
    public void updateDocumentAttribute(DocumentAttribute attribute, List<Long> directoryIds) {
        repository.updateDocumentAttribute(attribute, directoryIds);
    }

    @Override
    public void copy(Long dirId, String destinationDirectoryHash) {
        UserDirectory directory = repository.getById(dirId);
        UserDirectory copy = UserFileUtil.copyDirectory(directory);
        String copyName = directory.getName();
        String copyLocation = getLocation(directory) + copyName;
        String destinationDirLocation = getDestinationDirectoryLocation(directory, destinationDirectoryHash);

        if (!destinationDirLocation.startsWith(copyLocation)) {
//            if (getByFullNameAndOwner(directory.getOwner(), destinationDirectoryHash, copyName) != null) {
//                int matchesCount = repository.getLike(destinationDirectoryHash, copyName).size();
//                copy.setName(copyName + " (" + (matchesCount + 1) + ")");
//            }

            copy.setParentDirectoryHash(destinationDirectoryHash);
            copy.setHashName(UserFileUtil.createHashName());
            repository.save(copy);

            List<Object> docIds = userDocumentService.getActualIdsByParentDirectoryHash(directory.getHashName());
            docIds.forEach(id -> userDocumentService.copy((Long) id, copy.getHashName()));

            List<Object> dirIds = getActualIdsByParentDirectoryHash(directory.getHashName());
            dirIds.forEach(id -> copy((Long) id, copy.getHashName()));
        }
    }

    @Override
    public boolean copy(Long[] dirIds, String destinationDirectoryHash, User user) {
        Set<UserDirectory> directories = getByIds(Arrays.asList(dirIds));
        if (userDirectoryAccessService.isOwner(directories, user)) {
            Arrays.stream(dirIds).forEach(id -> copy(id, destinationDirectoryHash));
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash) {
        return repository.getPropertiesList("id", "parentDirectoryHash", parentDirectoryHash);
    }

    private String getDestinationDirectoryLocation(UserDirectory directory, String destinationDirHash) {
        UserDirectory destinationDir = null;
//        if (!directory.getOwner().getLogin().equals(destinationDirHash)) {
//            destinationDir = getByHashName(destinationDirHash);
//        }

        String destinationDirLocation = null;
        if (destinationDir != null) {
            destinationDirLocation = getLocation(destinationDir) + destinationDir.getName();
        } else {
//            destinationDirLocation = directory.getOwner().getLogin();
        }
        return destinationDirLocation;
    }
}
