package com.geekhub.services;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
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

    List<FriendsGroup> getByFriend(User friend);

    void update(List<FriendsGroup> groups);

    FriendsGroup getByOwnerAndName(User owner, String name);

    List<FriendsGroup> getListByOwner(User owner);
}
