package com.geekhub.service;

import com.geekhub.dao.DocumentOldVersionDao;
import com.geekhub.entity.DocumentOldVersion;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocumentOldVersionServiceImpl implements DocumentOldVersionService {

    @Autowired
    private DocumentOldVersionDao documentOldVersionDao;

    @Autowired
    private UserService userService;

    @Override
    public List<DocumentOldVersion> getAll(String orderParameter) {
        return documentOldVersionDao.getAll(orderParameter);
    }

    @Override
    public DocumentOldVersion getById(Long id) {
        return documentOldVersionDao.getById(id);
    }

    @Override
    public DocumentOldVersion get(String propertyName, Object value) {
        return documentOldVersionDao.get(propertyName, value);
    }

    @Override
    public Long save(DocumentOldVersion entity) {
        return documentOldVersionDao.save(entity);
    }

    @Override
    public void update(DocumentOldVersion entity) {
        documentOldVersionDao.update(entity);
    }

    @Override
    public void delete(DocumentOldVersion entity) {
        documentOldVersionDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        documentOldVersionDao.deleteById(entityId);
    }
}
