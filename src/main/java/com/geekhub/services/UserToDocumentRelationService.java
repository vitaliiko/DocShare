package com.geekhub.services;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.entities.enums.FileRelationType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface UserToDocumentRelationService extends EntityService<UserToDocumentRelation, Long> {

    List<UserToDocumentRelation> create(UserDocument document, List<User> users, FileRelationType relationType);

    UserToDocumentRelation create(UserDocument document, User user, FileRelationType relationType);

    void deleteByDocumentBesidesOwner(UserDocument document);

    List<UserToDocumentRelation> getAllByDocument(UserDocument document);

    List<User> getAllUsersByDocumentIdBesidesOwner(Long documentId);

    User getDocumentOwner(UserDocument document);

    List<User> getAllByDocumentIdAndRelation(UserDocument document, FileRelationType relationType);

    List<String> getAllDocumentHashNamesByOwner(User owner);

    Set<UserDocument> getAllAccessibleDocuments(User user);

    Set<UserDocument> getAllAccessibleDocumentsInRoot(User user, List<String> directoryHashes);

    UserDocument getDocumentByFullNameAndOwner(String parentDirHash, String docName, User owner);

    List<UserDocument> getAllDocumentsByFullNamesAndOwner(String parentDirHash, List<String> docNames, User owner);

    UserToDocumentRelation getByDocumentAndUser(UserDocument document, User user);

    Long getDocumentsCountByOwnerAndDocumentIds(User owner, Long[] documentIds);

    List<FileRelationType> getAllRelationsByDocumentsAndUser(List<UserDocument> documents, User user);
}
