package com.geekhub.services;

import com.geekhub.dto.*;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.DocumentAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.utils.DirectoryWrapper;
import org.springframework.stereotype.Service;

@Service
public interface UserDirectoryService extends EntityService<UserDirectory, Long> {

    UserDirectory createDirectory(User owner, String parentDirHash, String dirName);

    Set<UserDirectory> getAllByIds(Collection<Long> dirIds);

    Set<UserDirectory> getAllByIds(Long[] dirIds);

    List<UserDirectory> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long dirIds, Long removerId);

    void moveToTrash(Long[] dirIds, Long removerId);

    DirectoryWrapper createDirectoryWrapper(String directoryHash, User user);

    void replace(Set<UserDirectory> directories, String destinationDirectoryHash, User user);

    Long recover(Long removedDirId);

    void copyToRoot(Long directoryId, User user);

    void copy(Collection<UserDirectory> directories, String destinationDirectoryHash, User user);

    void recover(Long[] removedDirIds);

    UserDirectory getByNameAndOwnerId(Long ownerId, String name);

    UserDirectory getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);

    UserDirectory getByHashName(String hashName);

    List<UserDirectory> getAllByParentDirectoryHash(String parentDirectoryHash);

    List<UserDirectory> getAllByParentDirectoryHashes(List<String> parentDirectoryHashes);

    List<UserDirectory> getTreeByParentDirectoryHashes(Collection<String> parentDirectoryHashes);

    List<UserDirectory> getTreeByParentDirectoryHash(String parentDirectoryHashes);

    List<UserDirectory> getAllByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status);

    List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash);

    Set<User> getAllReadersAndEditors(Long docId);

    String getLocation(UserDirectory directory);

    Set<UserDirectory> getActualByOwner(User owner);

    Set<UserDirectory> searchByName(User owner, String name);

    UserDirectory renameDirectory(UserDirectory directory, String newName, User owner);

    UserDirectory shareDirectory(UserDirectory directory, SharedDto sharedDto, User user);

    DirectoryContentDto getDirectoryContent(String dirHashName, User user);

    void updateDocumentAttribute(DocumentAttribute attribute, List<Long> directoryIds);

    FileAccessDto findAllRelations(Long directoryId);

    boolean isDirectoryNameValid(String parentDirectoryHash, String dirName, User owner);

    List<String> getSimilarDirectoryNamesInDirectory(String directoryHash, Collection<UserDirectory> directories);

    DirectoryWrapper getAllDirectoryRelations(UserDirectory directory);

    ZipDto packDirectoriesToZIP(List<Long> docIds, List<Long> dirIds);
}
