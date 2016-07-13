package com.geekhub.repositories;

import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;

public interface UserToDocumentRelationRepository extends EntityRepository<UserToDocumentRelation, Long> {

    void deleteByDocumentBesidesOwner(UserDocument document);
}
