package com.geekhub.services;

import com.geekhub.entities.FriendGroupToDocumentRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.FileRelationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendGroupToDocumentRelationService extends EntityService<FriendGroupToDocumentRelation, Long> {

    List<FriendGroupToDocumentRelation> create(UserDocument document, List<FriendsGroup> groups,
                                               FileRelationType relationType);

    FriendGroupToDocumentRelation create(UserDocument document, FriendsGroup group, FileRelationType relationType);

    void deleteByDocument(UserDocument document);

    List<FriendsGroup> getAllGroupsByDocumentId(Long documentId);

    List<FriendsGroup> getAllGroupsByDocumentIdAndRelation(Long documentId, FileRelationType relationType);

    List<User> getAllGroupsMembersByDocumentId(Long documentId);

    List<FriendGroupToDocumentRelation> getAllByDocument(UserDocument document);

    Long getCountByFriendGroup(FriendsGroup group);
}
