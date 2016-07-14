package com.geekhub.repositories;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;

import java.util.List;

public interface UserToDocumentRelationRepository extends EntityRepository<UserToDocumentRelation, Long> {

    void deleteByDocumentBesidesOwner(UserDocument document);

    List<String> getAllDocumentHashNamesByOwner(User owner);
}
