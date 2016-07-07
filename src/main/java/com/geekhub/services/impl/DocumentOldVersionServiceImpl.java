package com.geekhub.services.impl;

import com.geekhub.dao.DocumentOldVersionDao;
import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.UserDocument;
import java.util.List;

import com.geekhub.services.DocumentOldVersionService;
import org.hibernate.Hibernate;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocumentOldVersionServiceImpl implements DocumentOldVersionService {

    @Inject
    private DocumentOldVersionDao documentOldVersionDao;

    @Override
    public List<DocumentOldVersion> getAll(String orderParameter) {
        return documentOldVersionDao.getAll(orderParameter);
    }

    @Override
    public DocumentOldVersion getById(Long id) {
        DocumentOldVersion oldVersion = documentOldVersionDao.getById(id);
        UserDocument document = oldVersion.getUserDocument();
        Hibernate.initialize(document.getDocumentOldVersions());
        return oldVersion;
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
