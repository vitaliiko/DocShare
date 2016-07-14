package com.geekhub.repositories;

import com.geekhub.entities.*;

import java.util.List;

public interface FriendGroupToDocumentRelationRepository extends EntityRepository<FriendGroupToDocumentRelation, Long> {

    void deleteByDocument(UserDocument document);

    Long getCountByFriendGroup(FriendsGroup group);

    List<User> getAllGroupsMembersByDocument(Long documentId);
}
