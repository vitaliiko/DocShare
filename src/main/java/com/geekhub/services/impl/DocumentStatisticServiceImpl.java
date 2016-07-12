package com.geekhub.services.impl;

import com.geekhub.repositories.DocumentStatisticRepository;
import com.geekhub.entities.DocumentStatistic;
import com.geekhub.services.DocumentStatisticService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DocumentStatisticServiceImpl implements DocumentStatisticService {
    
    @Inject
    private DocumentStatisticRepository repository;
    
    @Override
    public DocumentStatistic getByUserDocumentId(Long documentId) {
        return repository.getByUserDocumentId(documentId);
    }

    @Override
    public List<DocumentStatistic> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public DocumentStatistic getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public DocumentStatistic get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(DocumentStatistic entity) {
        return repository.save(entity);
    }

    @Override
    public void update(DocumentStatistic entity) {
        repository.update(entity);
    }

    @Override
    public void delete(DocumentStatistic entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }
}
