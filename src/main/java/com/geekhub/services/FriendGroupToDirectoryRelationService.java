package com.geekhub.services;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.FileRelationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendGroupToDirectoryRelationService extends EntityService<FriendGroupToDirectoryRelation, Long> {

    List<FriendGroupToDirectoryRelation> create(UserDirectory directory, List<FriendsGroup> groups,
                                                FileRelationType relationType);

    FriendGroupToDirectoryRelation create(UserDirectory directory, FriendsGroup group, FileRelationType relationType);

    void deleteAllByDirectory(UserDirectory directory);

    List<FriendGroupToDirectoryRelation> getAllByDirectory(UserDirectory directory);

    Long getCountByFriendGroup(FriendsGroup group);

    List<FriendsGroup> getAllGroupsByDirectoryIdAndRelation(UserDirectory directory, FileRelationType relationType);

    List<FileRelationType> getAllRelationsByDirectoryIdAndUser(Long directoryId, User user);
}
