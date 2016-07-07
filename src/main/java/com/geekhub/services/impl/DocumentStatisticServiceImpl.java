package com.geekhub.services.impl;

import com.geekhub.dao.DocumentStatisticDao;
import com.geekhub.entities.DocumentStatistic;
import com.geekhub.entities.DocumentStatistic;
import com.geekhub.services.DocumentStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DocumentStatisticServiceImpl implements DocumentStatisticService {
    
    @Autowired
    private DocumentStatisticDao documentStatisticDao;
    
    @Override
    public DocumentStatistic getByUserDocumentId(Long documentId) {
        return documentStatisticDao.getByUserDocumentId(documentId);
    }

    @Override
    public List<DocumentStatistic> getAll(String orderParameter) {
        return documentStatisticDao.getAll(orderParameter);
    }

    @Override
    public DocumentStatistic getById(Long id) {
        return documentStatisticDao.getById(id);
    }

    @Override
    public DocumentStatistic get(String propertyName, Object value) {
        return documentStatisticDao.get(propertyName, value);
    }

    @Override
    public Long save(DocumentStatistic entity) {
        return documentStatisticDao.save(entity);
    }

    @Override
    public void update(DocumentStatistic entity) {
        documentStatisticDao.update(entity);
    }

    @Override
    public void delete(DocumentStatistic entity) {
        documentStatisticDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        documentStatisticDao.deleteById(entityId);
    }
}
