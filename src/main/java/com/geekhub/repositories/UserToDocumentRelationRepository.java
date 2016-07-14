package com.geekhub.repositories;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;

import java.util.List;
import java.util.Set;

public interface UserToDocumentRelationRepository extends EntityRepository<UserToDocumentRelation, Long> {

    void deleteByDocumentBesidesOwner(UserDocument document);

    List<String> getAllDocumentHashNamesByOwner(User owner);

    User getDocumentOwner(UserDocument document);

    List<User> getAllUsersByDocumentIdBesidesOwner(Long documentId);

    List<UserDocument> getAllAccessibleDocuments(User user);

    List<UserDocument> getAllAccessibleDocumentsInRoot(User user, List<String> directoryHashes);
}
