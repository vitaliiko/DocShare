package com.geekhub.services;

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

    Long recover(Long removedDirId);

    void recover(Long[] removedDirIds);

    UserDirectory getByNameAndOwnerId(Long ownerId, String name);

    UserDirectory getByFullNameAndOwnerId(User owner, String parentDirectoryHash, String name);

    UserDirectory getByHashName(String hashName);

    List<UserDirectory> getAllByParentDirectoryHash(String parentDirectoryHash);

    Set<User> getAllReaders(Long docId);

    String getLocation(UserDirectory directory);

    Set<UserDirectory> getAllByOwnerAndAttribute(User owner, DocumentAttribute attribute);
}