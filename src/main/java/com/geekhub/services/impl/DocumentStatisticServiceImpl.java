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
    private DocumentStatisticRepository documentStatisticRepository;
    
    @Override
    public DocumentStatistic getByUserDocumentId(Long documentId) {
        return documentStatisticRepository.getByUserDocumentId(documentId);
    }

    @Override
    public List<DocumentStatistic> getAll(String orderParameter) {
        return documentStatisticRepository.getAll(orderParameter);
    }

    @Override
    public DocumentStatistic getById(Long id) {
        return documentStatisticRepository.getById(id);
    }

    @Override
    public DocumentStatistic get(String propertyName, Object value) {
        return documentStatisticRepository.get(propertyName, value);
    }

    @Override
    public Long save(DocumentStatistic entity) {
        return documentStatisticRepository.save(entity);
    }

    @Override
    public void update(DocumentStatistic entity) {
        documentStatisticRepository.update(entity);
    }

    @Override
    public void delete(DocumentStatistic entity) {
        documentStatisticRepository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        documentStatisticRepository.deleteById(entityId);
    }
}
