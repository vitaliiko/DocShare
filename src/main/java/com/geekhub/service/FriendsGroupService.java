package com.geekhub.service;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface FriendsGroupService extends EntityService<FriendsGroup, Long> {

    boolean addFriend(Long groupId, Long friendId);

    FriendsGroup getByName(String groupName);

    Set<User> getFriendsSet(FriendsGroup group);

    List<FriendsGroup> getFriendsGroups(User owner, String propertyName, Object value);

    List<FriendsGroup> getByOwnerAndFriend(User owner, User friend);

    Long addFriendsGroup(Long ownerId, String name, Long[] friendsIds);

    void update(Long groupId, Long ownerId, String groupName, Long[] friendsIds);

    FriendsGroup getWithFriends(Long groupId);

    List<FriendsGroup> getByFriend(User friend);

    void update(List<FriendsGroup> groups);
}
