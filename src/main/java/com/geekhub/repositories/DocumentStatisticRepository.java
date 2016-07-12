package com.geekhub.repositories;

import com.geekhub.entities.DocumentStatistic;

public interface DocumentStatisticRepository extends EntityRepository<DocumentStatistic, Long> {

    DocumentStatistic getByUserDocumentId(Long documentId);
}
