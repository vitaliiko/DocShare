package com.geekhub.repositories;

import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;

import java.util.List;

public interface FriendGroupToDocumentRelationRepository extends EntityRepository<FriendGroupToDocumentRelation, Long> {

    void deleteByDocument(UserDocument document);

    Long getCountByFriendGroup(FriendsGroup group);

    List<User> getAllGroupsMembersByDocument(Long documentId);

    List<FriendsGroup> getAllGroupsByDocumentId(Long documentId);

    List<FriendsGroup> getAllGroupsByDocumentIdAndRelation(Long documentId, FileRelationType relationType);

    List<FileRelationType> getAllRelationsByDocumentIdAndUser(Long documentId, User user);
}
