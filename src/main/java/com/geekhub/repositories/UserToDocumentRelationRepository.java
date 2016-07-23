package com.geekhub.repositories;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.entities.enums.FileRelationType;

import java.util.List;

public interface UserToDocumentRelationRepository extends EntityRepository<UserToDocumentRelation, Long> {

    void deleteAllBesidesOwnerByDocument(UserDocument document);

    List<String> getAllDocumentHashNamesByOwner(User owner);

    User getDocumentOwner(UserDocument document);

    List<User> getAllUsersByDocumentIdBesidesOwner(Long documentId);

    List<UserDocument> getAllAccessibleDocuments(User user);

    List<UserDocument> getAllAccessibleDocumentsInRoot(User user, List<String> directoryHashes);

    List<User> getAllByDocumentIdAndRelation(UserDocument document, FileRelationType relationType);

    UserDocument getDocumentByFullNameAndOwner(String parentDirHash, String docName, User owner);

    UserToDocumentRelation getByDocumentAndUser(UserDocument document, User user);

    Long getCountByOwnerAndDocumentIds(User owner, List<Long> idList);

    List<FileRelationType> getAllRelationsByDocumentsAndUser(List<UserDocument> documents, User user);

    List<UserDocument> getAllDocumentsByFullNamesAndOwner(String parentDirHash, List<String> docNames, User owner);
}
