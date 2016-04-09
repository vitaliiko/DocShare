package com.geekhub.service;

import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserDirectoryService extends EntityService<UserDirectory, Long> {

    List<UserDirectory> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long dirIds, Long removerId);

    void moveToTrash(Long[] dirIds, Long removerId);

    void recover(Long dirIds);

    void recover(Long[] dirIds);

    UserDirectory getByNameAndOwnerId(Long ownerId, String name);

    UserDirectory getByFullNameAndOwnerId(User owner, String parentDirectoryHash, String name);

    UserDirectory getDirectoryWithReaders(Long dirId);

    UserDirectory getByHashName(String hashName);

    List<UserDirectory> getAllByParentDirectoryHash(String parentDirectoryHash);
}
