package com.geekhub.services;

import com.geekhub.dao.UserDirectoryDao;
import com.geekhub.entities.RemovedDirectory;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.utils.UserFileUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private UserDocumentService userDocumentService;

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
        directory.setDocumentStatus(DocumentStatus.REMOVED);
        setRemovedStatus(directory);
        userDirectoryDao.update(directory);
    }

    private void setRemovedStatus(UserDirectory directory) {
        directory.setDocumentStatus(DocumentStatus.REMOVED);

        List<UserDocument> documents = userDocumentService.getAllByParentDirectoryHash(directory.getHashName());
        documents.forEach(d -> d.setDocumentStatus(DocumentStatus.REMOVED));

        List<UserDirectory> directories = getAllByParentDirectoryHash(directory.getHashName());
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
        directory.setDocumentStatus(DocumentStatus.ACTUAL);
        removedDirectoryService.delete(removedDocument);
        userDirectoryDao.update(directory);
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
    public List<UserDirectory> getAllByParentDirectoryHash(String parentDirectoryHash) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("documentStatus", DocumentStatus.ACTUAL);
        return userDirectoryDao.getList(propertiesMap);
    }

    @Override
    public UserDirectory getByHashName(String hashName) {
        return userDirectoryDao.get("hashName", hashName);
    }

    @Override
    public Set<UserDirectory> getByIds(List<Long> dirIds) {
        return new HashSet<>(userDirectoryDao.getAll("id", dirIds));
    }

    @Override
    public Set<User> getAllReaders(Long docId) {
        Set<User> users = new HashSet<>();
        UserDirectory document = getById(docId);
        users.addAll(document.getReaders());
        document.getReadersGroups().forEach(g -> users.addAll(g.getFriends()));
        users.add(document.getOwner());
        return users;
    }

    @Override
    public String getLocation(UserDirectory directory) {
        String location = "";
        String patentDirectoryHash = directory.getParentDirectoryHash();

        while(!patentDirectoryHash.equals(directory.getOwner().getLogin())) {
            UserDirectory dir = getByHashName(patentDirectoryHash);
            location = dir.getName() + "/" + location;
            patentDirectoryHash = dir.getParentDirectoryHash();
        }

        return location;
    }

    @Override
    public Set<UserDirectory> getAllByOwnerAndAttribute(User owner, DocumentAttribute attribute) {
        return new HashSet<>(userDirectoryDao.getList(owner, "documentAttribute", attribute));
    }
}
