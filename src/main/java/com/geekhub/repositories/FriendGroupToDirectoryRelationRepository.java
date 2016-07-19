package com.geekhub.repositories;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.FileRelationType;

import java.util.List;

public interface FriendGroupToDirectoryRelationRepository extends EntityRepository<FriendGroupToDirectoryRelation, Long> {

    void deleteByDirectoryBesidesOwner(UserDirectory directory);

    Long getCountByFriendGroup(FriendsGroup group);

    List<FriendsGroup> getAllGroupsByDirectoryIdAndRelation(Long directoryId, FileRelationType relationType);

    List<FileRelationType> getAllRelationsByDocumentIdAndUser(Long directoryId, User user);
}
