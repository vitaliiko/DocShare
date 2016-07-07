package com.geekhub.services;

import com.geekhub.entities.Comment;
import com.geekhub.entities.DocumentStatistic;
import org.springframework.stereotype.Service;

@Service
public interface DocumentStatisticService extends EntityService<DocumentStatistic, Long> {

    DocumentStatistic getByUserDocumentId(Long documentId);
}
