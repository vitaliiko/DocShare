package com.geekhub.service;

import com.geekhub.dao.UserDirectoryDao;
import com.geekhub.entity.RemovedDirectory;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.util.UserFileUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDirectoryServiceImpl implements UserDirectoryService {

    @Autowired
    private UserDirectoryDao userDirectoryDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RemovedDirectoryService removedDirectoryService;

    @Override
    public List<UserDirectory> getAll(String orderParameter) {
        return userDirectoryDao.getAll(orderParameter);
    }

    @Override
    public UserDirectory getById(Long id) {
        return userDirectoryDao.getById(id);
    }

    @Override
    public UserDirectory get(String propertyName, Object value) {
        return userDirectoryDao.get(propertyName, value);
    }

    @Override
    public Long save(UserDirectory entity) {
        return userDirectoryDao.save(entity);
    }

    @Override
    public void update(UserDirectory entity) {
        userDirectoryDao.update(entity);
    }

    @Override
    public void delete(UserDirectory entity) {
        userDirectoryDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        userDirectoryDao.deleteById(entityId);
    }

    @Override
    public List<UserDirectory> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return userDirectoryDao.getList("owner", owner);
    }

    @Override
    public void moveToTrash(Long docId, Long removerId) {
        UserDirectory directory = userDirectoryDao.getById(docId);
        RemovedDirectory removedDirectory = UserFileUtil.wrapUserDirectory(directory, removerId);
        removedDirectoryService.save(removedDirectory);
        directory.setOwner(null);
        userDirectoryDao.save(directory);
    }

    @Override
    public void moveToTrash(Long[] dirIds, Long removerId) {
        Arrays.stream(dirIds).forEach(id -> moveToTrash(id, removerId));
    }

    @Override
    public Long recover(Long removedDirIds) {
        RemovedDirectory removedDocument = removedDirectoryService.getById(removedDirIds);
        UserDirectory directory = removedDocument.getUserDirectory();
        User owner = removedDocument.getOwner();
        owner.getUserDirectories().add(directory);
        removedDirectoryService.delete(removedDocument);
        return directory.getId();
    }

    @Override
    public void recover(Long[] dirIds) {
        Arrays.stream(dirIds).forEach(this::recover);
    }

    @Override
    public UserDirectory getByNameAndOwnerId(Long ownerId, String name) {
        User owner = userService.getById(ownerId);
        return userDirectoryDao.get(owner, "name", name);
    }

    @Override
    public UserDirectory getByFullNameAndOwnerId(User owner, String parentDirectoryHash, String name) {
        Map<String, Object> propertiesMap = UserFileUtil.createPropertiesMap(owner, parentDirectoryHash, name);
        return userDirectoryDao.get(propertiesMap);
    }

    @Override
    public UserDirectory getDirectoryWithReaders(Long dirId) {
        UserDirectory directory = getById(dirId);
        Hibernate.initialize(directory.getReadersGroups());
        Hibernate.initialize(directory.getReaders());
        return directory;
    }

    @Override
    public List<UserDirectory> getAllByParentDirectoryHash(String parentDirectoryHash) {
        return userDirectoryDao.getList("parentDirectoryHash", parentDirectoryHash);
    }

    @Override
    public UserDirectory getByHashName(String hashName) {
        return userDirectoryDao.get("hashName", hashName);
    }
}
