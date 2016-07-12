package com.geekhub.services.impl;

import com.geekhub.repositories.DocumentOldVersionRepository;
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
    private DocumentOldVersionRepository repository;

    @Override
    public List<DocumentOldVersion> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public DocumentOldVersion getById(Long id) {
        DocumentOldVersion oldVersion = repository.getById(id);
        UserDocument document = oldVersion.getUserDocument();
        Hibernate.initialize(document.getDocumentOldVersions());
        return oldVersion;
    }

    @Override
    public DocumentOldVersion get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(DocumentOldVersion entity) {
        return repository.save(entity);
    }

    @Override
    public void update(DocumentOldVersion entity) {
        repository.update(entity);
    }

    @Override
    public void delete(DocumentOldVersion entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }
}
