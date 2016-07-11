package com.geekhub.services;

import com.geekhub.dto.SharedDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDirectory;
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

    void replace(Long dirId, String destinationDirectoryHash);

    boolean replace(Long[] dirIds, String destinationDirectoryHash, User user);

    Long recover(Long removedDirId);

    void copy(Long dirId, String destinationDirectoryHash);

    boolean copy(Long[] dirIds, String destinationDirectoryHash, User user);

    void recover(Long[] removedDirIds);

    UserDirectory getByNameAndOwnerId(Long ownerId, String name);

    UserDirectory getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);

    UserDirectory getByHashName(String hashName);

    List<UserDirectory> getByParentDirectoryHash(String parentDirectoryHash);

    List<UserDirectory> getByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status);

    List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash);

    Set<User> getAllReaders(Long docId);

    String getLocation(UserDirectory directory);

    Set<UserDirectory> getActualByOwner(User owner);

    Long getCountByFriendsGroup(FriendsGroup friendsGroup);

    Set<UserDirectory> searchByName(User owner, String name);

    UserDirectory renameDirectory(UserDirectory directory, String newName, User owner);

    UserDirectory shareDirectory(UserDirectory directory, SharedDto sharedDto, User user);

    Set<UserFileDto> getDirectoryContent(String dirHashName);
}
