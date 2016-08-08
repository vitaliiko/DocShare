package com.geekhub.repositories;

import com.geekhub.entities.UserDocumentStatistic;

public interface UserDocumentStatisticRepository extends EntityRepository<UserDocumentStatistic, Long> {

    UserDocumentStatistic getByUserDocumentId(Long documentId);
}
