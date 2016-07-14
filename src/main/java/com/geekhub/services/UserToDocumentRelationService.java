package com.geekhub.services;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.entities.enums.FileRelationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserToDocumentRelationService extends EntityService<UserToDocumentRelation, Long> {

    List<UserToDocumentRelation> create(UserDocument document, List<User> users, FileRelationType relationType);

    UserToDocumentRelation create(UserDocument document, User user, FileRelationType relationType);

    void deleteByDocumentBesidesOwner(UserDocument document);

    List<UserToDocumentRelation> getAllByDocument(UserDocument document);

    List<String> getAllDocumentHashNamesByOwner(User owner);
}
