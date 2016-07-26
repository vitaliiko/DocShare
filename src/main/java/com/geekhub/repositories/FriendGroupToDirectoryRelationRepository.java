package com.geekhub.repositories;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.FileRelationType;

import java.util.List;

public interface FriendGroupToDirectoryRelationRepository extends EntityRepository<FriendGroupToDirectoryRelation, Long> {

    void deleteAllByDirectory(UserDirectory directory);

    Long getCountByFriendGroup(FriendsGroup group);

    List<FriendsGroup> getAllGroupsByDirectoryIdAndRelation(UserDirectory directory, FileRelationType relationType);

    List<FileRelationType> getAllRelationsByDirectoryIdAndUser(Long directoryId, User user);

    List<UserDirectory> getAllAccessibleDirectories(User user);
}
