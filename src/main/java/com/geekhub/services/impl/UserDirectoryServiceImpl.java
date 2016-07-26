package com.geekhub.services.impl;

import com.geekhub.dto.*;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserDirectoryRepository;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.DirectoryWithRelations;
import com.geekhub.utils.CollectionTools;
import com.geekhub.utils.UserFileUtil;

import java.util.*;
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
                getAllByParentDirectoryHashAndStatus(directory.getHashName(), DocumentStatus.ACTUAL);
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
                getAllByParentDirectoryHashAndStatus(directory.getHashName(), DocumentStatus.REMOVED);
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
    public List<UserDirectory> getAllByParentDirectoryHashes(List<String> parentDirectoryHashes) {
        return repository.getList("parentDirectoryHash", parentDirectoryHashes);
    }

    @Override
    public List<UserDirectory> getTreeByParentDirectoryHash(String parentDirectoryHash) {
        List<UserDirectory> childDirectories = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        hashes.add(parentDirectoryHash);
        while (hashes.size() > 0) {
            List<UserDirectory> directories = getAllByParentDirectoryHashes(hashes);
            hashes = directories.stream().map(UserDirectory::getHashName).collect(Collectors.toList());
            childDirectories.addAll(directories);
        }
        return childDirectories;
    }

    @Override
    public List<UserDirectory> getAllByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status) {
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
    public UserDirectory createDirectory(User owner, String parentDirHash, String dirName) {

        UserDirectory directory = getByFullNameAndOwner(owner, parentDirHash, dirName);
        if (directory != null && !UserFileUtil.validateDirectoryName(dirName)) {
            return null;
        }
        directory = UserFileUtil.createUserDirectory(owner, parentDirHash, dirName);
        long dirId = save(directory);
        directory.setId(dirId);

        createRelations(directory, parentDirHash, owner);
        userToDirectoryRelationService.create(directory, owner, FileRelationType.OWN);
        return directory;
    }

    private DirectoryWithRelations createRelations(UserDirectory directory, String parentDirHash, User owner) {
        DirectoryWithRelations directoryWithRelations = new DirectoryWithRelations();
        directoryWithRelations.setDirectory(directory);
        directoryWithRelations.setOwner(owner);
        if (directory.getId() != null) {
            userToDirectoryRelationService.deleteAllBesidesOwnerByDirectory(directory);
            friendGroupToDirectoryRelationService.deleteAllByDirectory(directory);
        }
        if (!parentDirHash.equals("root") && !parentDirHash.equals(owner.getLogin())) {
            UserDirectory parentDirectory = getByHashName(parentDirHash);
            directoryWithRelations.addAllUserRelations(
                    userToDirectoryRelationService.getAllByDirectory(parentDirectory).stream()
                            .filter(r -> r.getFileRelationType() != FileRelationType.OWN)
                            .collect(Collectors.toList())
            );
            directoryWithRelations.getUserRelations().forEach(r -> {
                UserToDirectoryRelation relation = userToDirectoryRelationService
                        .create(directory, r.getUser(), r.getFileRelationType());
                directoryWithRelations.addRelation(relation);
            });

            directoryWithRelations.addAllGroupRelations(
                    friendGroupToDirectoryRelationService.getAllByDirectory(parentDirectory)
            );
            directoryWithRelations.getGroupRelations().forEach(r -> {
                FriendGroupToDirectoryRelation relation = friendGroupToDirectoryRelationService
                        .create(directory, r.getFriendsGroup(), r.getFileRelationType());
                directoryWithRelations.addRelation(relation);
            });
        }
        return directoryWithRelations;
    }

    @Override
    public Set<UserDirectory> getAllByIds(Collection<Long> dirIds) {
        HashSet<UserDirectory> directories = new HashSet<>();
        if (!CollectionUtils.isEmpty(dirIds)) {
            directories.addAll(repository.getAll("id", dirIds));
        }
        return directories;
    }

    @Override
    public Set<UserDirectory> getAllByIds(Long[] dirIds) {
        if (dirIds == null) {
            return new HashSet<>();
        }
        return getAllByIds(Arrays.asList(dirIds));
    }

    @Override
    public Set<User> getAllReadersAndEditors(Long docId) {
        return new HashSet<>();
    }

    @Override
    public String getLocation(UserDirectory directory) {
        String location = "";
        String patentDirectoryHash = directory.getParentDirectoryHash();
        User owner = userToDirectoryRelationService.getDirectoryOwner(directory);

        while(!patentDirectoryHash.equals(owner.getLogin())) {
            UserDirectory dir = getByHashName(patentDirectoryHash);
            location = dir.getName() + "/" + location;
            patentDirectoryHash = dir.getParentDirectoryHash();
        }

        return location;
    }

    @Override
    public Set<UserDirectory> getActualByOwner(User owner) {
        return new HashSet<>(repository.getList(owner, "documentAttribute", DocumentStatus.ACTUAL));
    }

    @Override
    public void replace(Set<UserDirectory> directories, String destinationDirectoryHash, User user) {
        if (CollectionUtils.isEmpty(directories)) {
            return;
        }
        UserDirectory destinationDir = null;
        DirectoryWithRelations relations = null;
        if (destinationDirectoryHash.equals("root")) {
            destinationDirectoryHash = user.getLogin();
        } else {
            destinationDir = getByHashName(destinationDirectoryHash);
            relations = getAllDirectoryRelations(destinationDir);
        }
        directories = setDirectoriesFullNames(destinationDirectoryHash, directories);
        for (UserDirectory dir : directories) {
            dir.setDocumentAttribute(destinationDir == null ? DocumentAttribute.PRIVATE : destinationDir.getDocumentAttribute());
            update(dir);
            userToDirectoryRelationService.deleteAllBesidesOwnerByDirectory(dir);
            createRelations(dir, relations);
            createRelationsForChilds(destinationDirectoryHash, relations);
        }
    }

    private void createRelationsForChilds(String dirHashName, DirectoryWithRelations relations) {
        List<UserDirectory> childDirectories = getTreeByParentDirectoryHash(dirHashName);
        List<String> parentDirHashList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(childDirectories)) {
            childDirectories.forEach(d -> {
                createRelations(d, relations);
                d.setDocumentAttribute(relations.getDocumentAttribute());
                update(d);
            });
            parentDirHashList = childDirectories.stream().map(UserDirectory::getHashName).collect(Collectors.toList());
        }
        parentDirHashList.add(dirHashName);
        List<UserDocument> childDocuments = userDocumentService.getAllByParentDirectoryHashes(parentDirHashList);
        userDocumentService.createRelations(childDocuments, relations);
    }

    @Override
    public void copy(Collection<UserDirectory> directories, String destinationDirectoryHash, User user) {
        if (CollectionUtils.isEmpty(directories)) {
            return;
        }
        UserDirectory destinationDir = null;
        if (destinationDirectoryHash.equals("root")) {
            destinationDirectoryHash = user.getLogin();
        } else {
            destinationDir = getByHashName(destinationDirectoryHash);
        }
        directories = setDirectoriesFullNames(destinationDirectoryHash, directories.stream().collect(Collectors.toSet()));
        for (UserDirectory dir : directories) {
            UserDirectory copiedDir = UserFileUtil.copyDirectory(dir);
            copiedDir.setDocumentAttribute(destinationDir == null ? DocumentAttribute.PRIVATE
                    : destinationDir.getDocumentAttribute());
            save(copiedDir);
            DirectoryWithRelations directoryWithRelations = createRelations(copiedDir, destinationDirectoryHash, user);
            userToDirectoryRelationService.create(copiedDir, user, FileRelationType.OWN);
            copyContent(dir, directoryWithRelations);
        }
    }

    private void copyContent(UserDirectory originalDir, DirectoryWithRelations destinationDir) {
        List<UserDocument> containedDocuments =
                userDocumentService.getAllByParentDirectoryHashAndStatus(originalDir.getHashName(), DocumentStatus.ACTUAL);
        userDocumentService.copy(containedDocuments, destinationDir);

        List<UserDirectory> containedDirectories =
                getAllByParentDirectoryHashAndStatus(originalDir.getHashName(), DocumentStatus.ACTUAL);
        copy(containedDirectories, destinationDir);
    }

    private Set<UserDirectory> setDirectoriesFullNames(String destinationDirectoryHash, Set<UserDirectory> directories) {
        List<String> similarDocNames = getSimilarDirectoryNamesInDirectory(destinationDirectoryHash, directories);
        directories.stream().filter(dir -> similarDocNames.contains(dir.getName())).forEach(dir -> {
            int documentIndex = UserFileUtil.countFileNameIndex(similarDocNames, dir);
            String newDirName = dir.getName() + " (" + documentIndex + ")";
            dir.setName(newDirName);
            similarDocNames.add(newDirName);
        });
        directories.forEach(d -> d.setParentDirectoryHash(destinationDirectoryHash));
        return directories;
    }

    @Override
    public void copy(Collection<UserDirectory> directories, DirectoryWithRelations destinationDir) {
        for (UserDirectory dir : directories) {
            UserDirectory copiedDir = UserFileUtil.copyDirectory(dir);
            copiedDir.setDocumentAttribute(destinationDir.getDocumentAttribute());
            copiedDir.setParentDirectoryHash(destinationDir.getHashName());
            save(copiedDir);
            createRelations(copiedDir, destinationDir);
            userToDirectoryRelationService.create(copiedDir, destinationDir.getOwner(), FileRelationType.OWN);
            destinationDir.setDirectory(copiedDir);
            copyContent(dir, destinationDir);
        }
    }

    private void createRelations(UserDirectory directory, DirectoryWithRelations relations) {
        if (relations == null) {
            return;
        }
        deleteRelations(directory);
        if (!CollectionUtils.isEmpty(relations.getUserRelations())) {
            relations.getUserRelations().forEach(r -> userToDirectoryRelationService
                    .create(directory, r.getUser(), r.getFileRelationType()));
        }
        if (!CollectionUtils.isEmpty(relations.getGroupRelations())) {
            relations.getGroupRelations().forEach(r -> friendGroupToDirectoryRelationService
                    .create(directory, r.getFriendsGroup(), r.getFileRelationType()));
        }
        if (!CollectionUtils.isEmpty(relations.getEditors())) {
            userToDirectoryRelationService.create(directory, relations.getEditors(), FileRelationType.EDIT);
        }
        if (!CollectionUtils.isEmpty(relations.getReaders())) {
            userToDirectoryRelationService.create(directory, relations.getReaders(), FileRelationType.READ);
        }
        if (!CollectionUtils.isEmpty(relations.getEditorGroups())) {
            friendGroupToDirectoryRelationService.create(directory, relations.getEditorGroups(), FileRelationType.EDIT);
        }
        if (!CollectionUtils.isEmpty(relations.getReaderGroups())) {
            friendGroupToDirectoryRelationService.create(directory, relations.getReaderGroups(), FileRelationType.READ);
        }
    }

    private void deleteRelations(UserDirectory directory) {
        if (directory.getId() != null) {
            userToDirectoryRelationService.deleteAllBesidesOwnerByDirectory(directory);
            friendGroupToDirectoryRelationService.deleteAllByDirectory(directory);
        }
    }

    @Override
    public List<String> getSimilarDirectoryNamesInDirectory(String directoryHash, Set<UserDirectory> directories) {
        if (CollectionUtils.isEmpty(directories)) {
            return new ArrayList<>();
        }
        List<String> documentNames = directories.stream()
                .map(UserDirectory::getName)
                .collect(Collectors.toList());
        String pattern = UserFileUtil.createNamesPattern(documentNames);
        return repository.getSimilarDirectoryNamesInDirectory(directoryHash, pattern);
    }

    @Override
    public DirectoryWithRelations getAllDirectoryRelations(UserDirectory directory) {
        if (directory == null) {
            return new DirectoryWithRelations();
        }
        DirectoryWithRelations directoryWithRelations = new DirectoryWithRelations();
        directoryWithRelations.setDirectory(directory);

        List<UserToDirectoryRelation> userRelations = userToDirectoryRelationService.getAllByDirectory(directory);
        directoryWithRelations.addAllUserRelations(userRelations.stream()
                        .filter(r -> r.getFileRelationType() != FileRelationType.OWN)
                        .collect(Collectors.toList()));
        User owner = CollectionTools.getDifferenceObject(userRelations, directoryWithRelations.getUserRelations()).getUser();
        directoryWithRelations.setOwner(owner);

        directoryWithRelations.addAllGroupRelations(
                friendGroupToDirectoryRelationService.getAllByDirectory(directory)
        );
        return directoryWithRelations;
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

        DirectoryWithRelations relations = convertSharedDtoToRelations(sharedDto);
        createRelations(directory, relations);
        createRelationsForChilds(directory.getHashName(), relations);

        sendEvents(directory, user, currentReadersAndEditors);
        return directory;
    }

    private DirectoryWithRelations convertSharedDtoToRelations(SharedDto shared) {
        DirectoryWithRelations relations = new DirectoryWithRelations();
        relations.setDocumentAttribute(DocumentAttribute.getValue(shared.getAccess()));
        List<User> users = userService.getByIds(CollectionTools.unionLists(shared.getReaders(), shared.getEditors()));

        List<User> editors = CollectionTools.filterUserList(users, shared.getEditors());
        relations.addAllEditors(editors);

        List<User> readers = CollectionTools.filterUserList(users, shared.getReaders());
        relations.addAllReaders(readers);

        List<FriendsGroup> groups = friendGroupService
                .getByIds(CollectionTools.unionLists(shared.getReaderGroups(), shared.getEditorGroups()));

        List<FriendsGroup> editorGroups = CollectionTools.filterGroupList(groups, shared.getEditorGroups());
        relations.addAllEditorGroups(editorGroups);

        List<FriendsGroup> readerGroups = CollectionTools.filterGroupList(groups, shared.getReaderGroups());
        relations.addAllReaderGroups(readerGroups);
        return relations;
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
    public DirectoryContentDto getDirectoryContent(String dirHashName, User user) {
        DirectoryContentDto contentDto = new DirectoryContentDto();
        contentDto.setDirHashName(dirHashName);
        if (dirHashName.equals("root")) {
            dirHashName = user.getLogin();
        } else if (!dirHashName.equals(user.getLogin())) {
            UserDirectory directory = getByHashName(dirHashName);
            contentDto.setParentDirHashName(directory.getParentDirectoryHash());
        }

        List<UserDocument> documents =
                userDocumentService.getAllByParentDirectoryHashAndStatus(dirHashName, DocumentStatus.ACTUAL);
        List<UserDirectory> directories = getAllByParentDirectoryHashAndStatus(dirHashName, DocumentStatus.ACTUAL);

        if (!CollectionUtils.isEmpty(documents)) {
            documents.forEach(d -> contentDto.addFile(EntityToDtoConverter.convert(d)));
        }
        if (!CollectionUtils.isEmpty(directories)) {
            directories.forEach(d -> contentDto.addFile(EntityToDtoConverter.convert(d)));
        }
        return contentDto;
    }

    @Override
    public void updateDocumentAttribute(DocumentAttribute attribute, List<Long> directoryIds) {
        repository.updateDocumentAttribute(attribute, directoryIds);
    }

    @Override
    public FileAccessDto findAllRelations(Long directoryId) {
        UserDirectory directory = getById(directoryId);
        List<User> editors = userToDirectoryRelationService.getAllByDirectoryIdAndRelation(directory, FileRelationType.EDIT);
        List<User> readers = userToDirectoryRelationService.getAllByDirectoryIdAndRelation(directory, FileRelationType.READ);

        List<FriendsGroup> editorGroups = friendGroupToDirectoryRelationService
                .getAllGroupsByDirectoryIdAndRelation(directory, FileRelationType.EDIT);
        List<FriendsGroup> readerGroups = friendGroupToDirectoryRelationService
                .getAllGroupsByDirectoryIdAndRelation(directory, FileRelationType.READ);

        FileAccessDto fileDto = new FileAccessDto();
        fileDto.setAttribute(directory.getDocumentAttribute());
        fileDto.setReaders(EntityToDtoConverter.convertToBaseUserDtos(readers));
        fileDto.setEditors(EntityToDtoConverter.convertToBaseUserDtos(editors));
        fileDto.setReaderGroups(EntityToDtoConverter.convertToFriendGroupDtos(readerGroups));
        fileDto.setEditorGroups(EntityToDtoConverter.convertToFriendGroupDtos(editorGroups));
        return fileDto;
    }

    @Override
    public boolean isDirectoryNameValid(String parentDirectoryHash, String dirName, User owner) {
        UserDirectory existingDirectory = getByFullNameAndOwner(owner, parentDirectoryHash, dirName);
        return existingDirectory == null && !UserFileUtil.validateDocumentNameWithoutExtension(dirName);
    }

    @Override
    public List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash) {
        return repository.getPropertiesList("id", "parentDirectoryHash", parentDirectoryHash);
    }
}
