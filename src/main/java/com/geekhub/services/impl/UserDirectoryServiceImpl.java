package com.geekhub.services.impl;

import com.geekhub.dto.*;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.exceptions.FileOperationException;
import com.geekhub.repositories.UserDirectoryRepository;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.DirectoryWrapper;
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
    public List<UserDirectory> getTreeByParentDirectoryHash(String parentDirectoryHashes) {
        List<String> hashes = Collections.singletonList(parentDirectoryHashes);
        return getAllByParentDirectoryHashes(hashes);
    }

    @Override
    public List<UserDirectory> getTreeByParentDirectoryHashes(Collection<String> parentDirectoryHashes) {
        List<UserDirectory> childDirectories = new ArrayList<>();
        List<String> hashes = new ArrayList<>(parentDirectoryHashes);
        while (hashes.size() > 0) {
            List<UserDirectory> directories = getAllByParentDirectoryHashes(hashes);
            hashes = extractHashes(directories);
            childDirectories.addAll(directories);
        }
        return childDirectories;
    }

    private List<String> extractHashes(Collection<UserDirectory> directories) {
        return directories.stream().map(UserDirectory::getHashName).collect(Collectors.toList());
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

        UserDirectory existingDirectory = getByFullNameAndOwner(owner, parentDirHash, dirName);
        if (existingDirectory != null && !UserFileUtil.validateDirectoryName(dirName)) {
            return null;
        }
        UserDirectory newDirectory = UserFileUtil.createUserDirectory(owner, parentDirHash, dirName);
        long dirId = save(newDirectory);
        newDirectory.setId(dirId);

        UserDirectory parentDirectory = getByHashName(parentDirHash);
        DirectoryWrapper directoryWrapper = getAllDirectoryRelations(parentDirectory);
        createRelations(newDirectory, directoryWrapper);
        userToDirectoryRelationService.create(newDirectory, owner, FileRelationType.OWN);
        return newDirectory;
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
    public void replace(Set<UserDirectory> directories, String destinationDirectoryHash, User user)
            throws FileOperationException {

        if (CollectionUtils.isEmpty(directories)) {
            return;
        }
        DirectoryWrapper destinationDirWrapper = createDirectoryWrapper(destinationDirectoryHash, user);
        directories = setDirectoriesFullNames(destinationDirWrapper.getHashName(), directories);
        for (UserDirectory dir : directories) {
            List<UserDirectory> childDirectories = getTreeByParentDirectoryHash(dir.getHashName());
            canCopyOrReplace(destinationDirWrapper.getHashName(), dir, childDirectories);
            dir.setDocumentAttribute(destinationDirWrapper.getDocumentAttribute());
            update(dir);
            createRelations(dir, destinationDirWrapper);
            createRelationsForChilds(childDirectories, destinationDirWrapper.getHashName(), destinationDirWrapper);
        }
    }

    @Override
    public DirectoryWrapper createDirectoryWrapper(String destinationDirectoryHash, User user) {
        DirectoryWrapper directory = new DirectoryWrapper();
        if (destinationDirectoryHash.equals("root")) {
            destinationDirectoryHash = user.getLogin();
            directory.setHashName(destinationDirectoryHash);
        } else if (destinationDirectoryHash.endsWith(user.getLogin())) {
            directory.setHashName(destinationDirectoryHash);
        } else {
            UserDirectory destinationDir = getByHashName(destinationDirectoryHash);
            directory = getAllDirectoryRelations(destinationDir);
        }
        directory.setOwner(user);
        return directory;
    }

    private void createRelationsForChilds(String dirHashName, DirectoryWrapper directory) {
        List<UserDirectory> childDirectories = getTreeByParentDirectoryHash(dirHashName);
        createRelationsForChilds(childDirectories, dirHashName, directory);
    }

    private void createRelationsForChilds(List<UserDirectory> childDirectories, String dirHashName, DirectoryWrapper directory) {
        List<String> parentDirHashList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(childDirectories)) {
            childDirectories.forEach(d -> {
                createRelations(d, directory);
                d.setDocumentAttribute(directory == null ? DocumentAttribute.PRIVATE : directory.getDocumentAttribute());
                update(d);
            });
            parentDirHashList = extractHashes(childDirectories);
        }
        parentDirHashList.add(dirHashName);
        List<UserDocument> childDocuments = userDocumentService.getAllByParentDirectoryHashes(parentDirHashList);
        userDocumentService.createRelations(childDocuments, directory);
    }

    @Override
    public void copyToRoot(Long directoryId, User user) {
        UserDirectory directory = getById(directoryId);
        List<UserDirectory> directories = new ArrayList<>();
        directories.add(directory);
        copy(directories, "root", user);
    }

    @Override
    public void copy(Collection<UserDirectory> directories, String destinationDirectoryHash, User user)
            throws FileOperationException {

        if (CollectionUtils.isEmpty(directories)) {
            return;
        }
        DirectoryWrapper destinationDirWrapper = createDirectoryWrapper(destinationDirectoryHash, user);
        Set<UserDirectory> directorySet = directories.stream().collect(Collectors.toSet());
        directories = setDirectoriesFullNames(destinationDirWrapper.getHashName(), directorySet);
        for (UserDirectory dir : directories) {
            List<UserDirectory> childDirectories = getTreeByParentDirectoryHash(dir.getHashName());
            canCopyOrReplace(destinationDirWrapper.getHashName(), dir, childDirectories);

            UserDirectory copiedDir = UserFileUtil.copyDirectory(dir);
            copiedDir.setDocumentAttribute(destinationDirWrapper.getDocumentAttribute());
            save(copiedDir);

            createRelations(copiedDir, destinationDirWrapper);
            userToDirectoryRelationService.create(copiedDir, user, FileRelationType.OWN);
            destinationDirWrapper.setDirectory(copiedDir);
            copyContent(dir, destinationDirWrapper);
        }
    }

    private void canCopyOrReplace(String destinationDirectoryHash, UserDirectory dir, List<UserDirectory> childDirectories)
            throws FileOperationException {

        List<String> childHashes = extractHashes(childDirectories);
        childHashes.add(dir.getHashName());
        if (childHashes.contains(destinationDirectoryHash)) {
            throw new FileOperationException("You cannot copy/replace directory to itself");
        }
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

    private void copyContent(UserDirectory originalDir, DirectoryWrapper destinationDirWrapper) {
        List<UserDocument> containedDocuments = userDocumentService
                .getAllByParentDirectoryHashAndStatus(originalDir.getHashName(), DocumentStatus.ACTUAL);
        userDocumentService.copy(containedDocuments, destinationDirWrapper);

        List<UserDirectory> containedDirectories =
                getAllByParentDirectoryHashAndStatus(originalDir.getHashName(), DocumentStatus.ACTUAL);
        copyContainedDirectories(containedDirectories, destinationDirWrapper);
    }

    public void copyContainedDirectories(Collection<UserDirectory> directories, DirectoryWrapper destinationDir) {
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

    private void createRelations(UserDirectory directory, DirectoryWrapper relations) {
        if (directory == null) {
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

    private List<String> getSimilarDirectoryNamesInDirectory(String directoryHash, UserDirectory directory) {
        List<UserDirectory> directories = new ArrayList<>();
        directories.add(directory);
        return getSimilarDirectoryNamesInDirectory(directoryHash, directories);
    }

    @Override
    public List<String> getSimilarDirectoryNamesInDirectory(String directoryHash, Collection<UserDirectory> directories) {
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
    public DirectoryWrapper getAllDirectoryRelations(UserDirectory directory) {
        DirectoryWrapper directoryWrapper;
        if (directory == null) {
            directoryWrapper = new DirectoryWrapper();
            directoryWrapper.setDocumentAttribute(DocumentAttribute.PRIVATE);
            return directoryWrapper;
        }
        directoryWrapper = new DirectoryWrapper();
        directoryWrapper.setDirectory(directory);

        List<UserToDirectoryRelation> userRelations = userToDirectoryRelationService.getAllByDirectory(directory);
        directoryWrapper.addAllUserRelations(userRelations.stream()
                        .filter(r -> r.getFileRelationType() != FileRelationType.OWN)
                        .collect(Collectors.toList()));
        User owner = CollectionTools.getDifferenceObject(userRelations, directoryWrapper.getUserRelations()).getUser();
        directoryWrapper.setOwner(owner);

        directoryWrapper.addAllGroupRelations(
                friendGroupToDirectoryRelationService.getAllByDirectory(directory)
        );
        return directoryWrapper;
    }

    @Override
    public ZipDto packDirectoriesToZIP(List<Long> docIds, List<Long> dirIds) {
        Set<UserDirectory> rootDirectories = getAllByIds(dirIds);
        Set<UserDocument> documents = userDocumentService.getAllByIds(docIds);
        checkFilesDownloadOperation(documents, rootDirectories);

        Map<String, List<UserDocument>> documentMap = new HashMap<>();
        documentMap.put("", documents.stream().collect(Collectors.toList()));

        Set<String> dirHashes = new HashSet<>(extractHashes(rootDirectories));
        List<UserDirectory> childDirectories = getTreeByParentDirectoryHashes(dirHashes);
        dirHashes.addAll(extractHashes(childDirectories));
        List<UserDocument> childDocuments = userDocumentService.getAllByParentDirectoryHashes(dirHashes);

        while (childDocuments.size() > 0) {
            UserDocument doc = childDocuments.stream().findFirst().get();
            List<UserDocument> docInSameDir = childDocuments.stream()
                    .filter(d -> d.getParentDirectoryHash().equals(doc.getParentDirectoryHash()))
                    .collect(Collectors.toList());
            String location = userDocumentService.getLocation(doc);

            documentMap.put(location, docInSameDir);
            childDocuments.removeAll(docInSameDir);
        }

        documentMap.size();
        return null;
    }

    private void checkFilesDownloadOperation(Set<UserDocument> documents, Set<UserDirectory> directories) {
        String parentDirectoryHash = documents.stream().findFirst().get().getParentDirectoryHash();
        if (!documents.stream().allMatch(d -> d.getParentDirectoryHash().equals(parentDirectoryHash))
                && !directories.stream().allMatch(d -> d.getParentDirectoryHash().endsWith(parentDirectoryHash))) {
            throw new FileOperationException("Files and folders must be in one folder");
        }
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

        DirectoryWrapper relations = convertSharedDtoToRelations(sharedDto);
        createRelations(directory, relations);
        createRelationsForChilds(directory.getHashName(), relations);

        sendEvents(directory, user, currentReadersAndEditors);
        return directory;
    }

    private DirectoryWrapper convertSharedDtoToRelations(SharedDto shared) {
        DirectoryWrapper directory = new DirectoryWrapper();
        directory.setDocumentAttribute(DocumentAttribute.getValue(shared.getAccess()));
        List<User> users = userService.getByIds(CollectionTools.unionLists(shared.getReaders(), shared.getEditors()));

        List<User> editors = CollectionTools.filterUserList(users, shared.getEditors());
        directory.addAllEditors(editors);

        List<User> readers = CollectionTools.filterUserList(users, shared.getReaders());
        directory.addAllReaders(readers);

        List<FriendsGroup> groups = friendGroupService
                .getByIds(CollectionTools.unionLists(shared.getReaderGroups(), shared.getEditorGroups()));

        List<FriendsGroup> editorGroups = CollectionTools.filterGroupList(groups, shared.getEditorGroups());
        directory.addAllEditorGroups(editorGroups);

        List<FriendsGroup> readerGroups = CollectionTools.filterGroupList(groups, shared.getReaderGroups());
        directory.addAllReaderGroups(readerGroups);
        return directory;
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
        List<User> editors = userToDirectoryRelationService
                .getAllUsersByDirectoryIdAndRelation(directory, FileRelationType.EDIT);
        List<User> readers = userToDirectoryRelationService
                .getAllUsersByDirectoryIdAndRelation(directory, FileRelationType.READ);

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
