package com.geekhub.service;

import com.geekhub.dao.UserDocumentDao;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import com.geekhub.enums.DocumentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
@Transactional
public class UserDocumentServiceImpl implements UserDocumentService {

    @Autowired
    private UserDocumentDao userDocumentDao;

    @Autowired
    private UserService userService;

    @Override
    public List<UserDocument> getAll(String orderParameter) {
        return userDocumentDao.getAll(orderParameter);
    }

    @Override
    public UserDocument getById(Long id) {
        return userDocumentDao.getById(id);
    }

    @Override
    public UserDocument get(String propertyName, Object value) {
        return userDocumentDao.get(propertyName, value);
    }

    @Override
    public Long save(UserDocument entity) {
        return userDocumentDao.save(entity);
    }

    @Override
    public void update(UserDocument entity) {
        userDocumentDao.update(entity);
    }

    @Override
    public void delete(UserDocument entity) {
        userDocumentDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        userDocumentDao.deleteById(entityId);
    }

    @Override
    public List<UserDocument> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return userDocumentDao.getList("owner", owner);
    }

    @Override
    public List<UserDocument> getActualByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return userDocumentDao.getList(owner, "documentStatus", DocumentStatus.ACTUAL);
    }

    @Override
    public List<UserDocument> getRemovedByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return userDocumentDao.getList(owner, "documentStatus", DocumentStatus.REMOVED);
    }

    @Override
    public void moveToTrash(Long docId) {
        UserDocument document = userDocumentDao.getById(docId);
        document.setDocumentStatus(DocumentStatus.REMOVED);
        document.setLastModifyTime(Calendar.getInstance().getTime());
        userDocumentDao.update(document);
    }

    @Override
    public void recover(Long docId) {
        UserDocument document = userDocumentDao.getById(docId);
        document.setDocumentStatus(DocumentStatus.ACTUAL);
        document.setLastModifyTime(Calendar.getInstance().getTime());
        userDocumentDao.update(document);
    }
}
