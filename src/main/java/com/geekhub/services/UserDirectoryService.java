package com.geekhub.services;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDirectory;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.DocumentAttribute;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface UserDirectoryService extends EntityService<UserDirectory, Long> {

    Set<UserDirectory> getByIds(List<Long> dirIds);

    List<UserDirectory> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long dirIds, Long removerId);

    void moveToTrash(Long[] dirIds, Long removerId);

    void replace(Long dirId, String destinationDirectoryHash);

    void replace(Long[] dirIds, String destinationDirectoryHash);

    Long recover(Long removedDirId);

    void recover(Long[] removedDirIds);

    UserDirectory getByNameAndOwnerId(Long ownerId, String name);

    UserDirectory getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);

    UserDirectory getByHashName(String hashName);

    List<UserDirectory> getActualByParentDirectoryHash(String parentDirectoryHash);

    List<UserDirectory> getRemovedByParentDirectoryHash(String parentDirectoryHash);

    Set<User> getAllReaders(Long docId);

    String getLocation(UserDirectory directory);

    Set<UserDirectory> getActualByOwner(User owner);

    Long getCountByFriendsGroup(FriendsGroup friendsGroup);

    Set<UserDirectory> searchByName(User owner, String name);
}
