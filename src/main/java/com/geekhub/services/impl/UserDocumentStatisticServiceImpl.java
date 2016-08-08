package com.geekhub.services.impl;

import com.geekhub.entities.enums.StatisticField;
import com.geekhub.repositories.UserDocumentStatisticRepository;
import com.geekhub.entities.UserDocumentStatistic;
import com.geekhub.services.UserDocumentStatisticService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserDocumentStatisticServiceImpl implements UserDocumentStatisticService {
    
    @Inject
    private UserDocumentStatisticRepository repository;
    
    @Override
    public UserDocumentStatistic getByUserDocumentId(Long documentId) {
        return repository.getByUserDocumentId(documentId);
    }

    @Override
    public void addTimes(Long statId, StatisticField field) {
        UserDocumentStatistic statistic = getById(statId);
        if (field == StatisticField.ALL) {
            statistic.incAll();
        } else if (field == StatisticField.VIEW) {
            statistic.incAllViews();
        } else if (field == StatisticField.DOWNLOAD) {
            statistic.incAllDownloads();
        } else if (field == StatisticField.LAST_VERSION_VIEW) {
            statistic.incLastVersionViews();
            statistic.incAllViews();
        } else if (field == StatisticField.LAST_VERSION_DOWNLOAD) {
            statistic.incLastVersionDownloads();
            statistic.incAllDownloads();
        }
        update(statistic);
    }

    @Override
    public List<UserDocumentStatistic> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public UserDocumentStatistic getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public UserDocumentStatistic get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(UserDocumentStatistic entity) {
        return repository.save(entity);
    }

    @Override
    public void update(UserDocumentStatistic entity) {
        repository.update(entity);
    }

    @Override
    public void delete(UserDocumentStatistic entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }
}
