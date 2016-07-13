package com.geekhub.repositories;

import com.geekhub.entities.FriendGroupToDocumentRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;

public interface FriendGroupToDocumentRelationRepository extends EntityRepository<FriendGroupToDocumentRelation, Long> {

    void deleteByDocumentBesidesOwner(UserDocument document);

    Long getCountByFriendGroup(FriendsGroup group);
}
