package com.geekhub.services;

import com.geekhub.dto.DirectoryContentDto;
import com.geekhub.dto.FileAccessDto;
import com.geekhub.dto.SharedDto;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.DocumentAttribute;
import java.util.List;
import java.util.Set;

import com.geekhub.entities.enums.DocumentStatus;
import org.springframework.stereotype.Service;

@Service
public interface UserDirectoryService extends EntityService<UserDirectory, Long> {

    UserDirectory createDirectory(User owner, String parentDirHash, String dirName);

    Set<UserDirectory> getByIds(List<Long> dirIds);

    List<UserDirectory> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long dirIds, Long removerId);

    void moveToTrash(Long[] dirIds, Long removerId);

    void replace(Set<UserDirectory> directories, String destinationDirectoryHash, User user);

    Long recover(Long removedDirId);

    void copy(Long dirId, String destinationDirectoryHash);

    boolean copy(Long[] dirIds, String destinationDirectoryHash, User user);

    void recover(Long[] removedDirIds);

    UserDirectory getByNameAndOwnerId(Long ownerId, String name);

    UserDirectory getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);

    UserDirectory getByHashName(String hashName);

    List<UserDirectory> getAllByParentDirectoryHash(String parentDirectoryHash);

    List<UserDirectory> getTreeByParentDirectoryHash(String parentDirectoryHash);

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

    List<String> getSimilarDirectoryNamesInDirectory(String directoryHash, Set<UserDirectory> directories);
}
