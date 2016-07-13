package com.geekhub.repositories;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.UserDirectory;

public interface FriendGroupToDirectoryRelationRepository extends EntityRepository<FriendGroupToDirectoryRelation, Long> {

    void deleteByDirectoryBesidesOwner(UserDirectory directory);

    Long getCountByFriendGroup(FriendsGroup group);
}
