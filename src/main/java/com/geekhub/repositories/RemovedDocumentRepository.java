package com.geekhub.repositories;

import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;

public interface RemovedDocumentRepository extends EntityRepository<RemovedDocument, Long> {
    RemovedDocument get(User owner, String propertyName, Object value);

    RemovedDocument getByUserDocumentHashName(String userDocumentHashName);
}
