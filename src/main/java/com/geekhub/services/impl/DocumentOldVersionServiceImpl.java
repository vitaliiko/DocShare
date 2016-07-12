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
    private DocumentOldVersionRepository documentOldVersionRepository;

    @Override
    public List<DocumentOldVersion> getAll(String orderParameter) {
        return documentOldVersionRepository.getAll(orderParameter);
    }

    @Override
    public DocumentOldVersion getById(Long id) {
        DocumentOldVersion oldVersion = documentOldVersionRepository.getById(id);
        UserDocument document = oldVersion.getUserDocument();
        Hibernate.initialize(document.getDocumentOldVersions());
        return oldVersion;
    }

    @Override
    public DocumentOldVersion get(String propertyName, Object value) {
        return documentOldVersionRepository.get(propertyName, value);
    }

    @Override
    public Long save(DocumentOldVersion entity) {
        return documentOldVersionRepository.save(entity);
    }

    @Override
    public void update(DocumentOldVersion entity) {
        documentOldVersionRepository.update(entity);
    }

    @Override
    public void delete(DocumentOldVersion entity) {
        documentOldVersionRepository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        documentOldVersionRepository.deleteById(entityId);
    }
}
