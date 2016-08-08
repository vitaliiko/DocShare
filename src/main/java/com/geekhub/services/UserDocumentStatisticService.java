package com.geekhub.services;

import com.geekhub.entities.UserDocumentStatistic;
import com.geekhub.entities.enums.StatisticField;
import org.springframework.stereotype.Service;

@Service
public interface UserDocumentStatisticService extends EntityService<UserDocumentStatistic, Long> {

    UserDocumentStatistic getByUserDocumentId(Long documentId);

    void addTimes(Long statId, StatisticField field);
}
