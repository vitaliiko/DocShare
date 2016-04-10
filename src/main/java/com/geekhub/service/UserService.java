package com.geekhub.service;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface UserService extends EntityService<User, Long> {

    User getByLogin(String login);

    Set<User> getFriends(Long userId);

    FriendsGroup getFriendsGroupByName(Long ownerId, String groupName);

    void addFriendsGroup(Long ownerId, FriendsGroup group);

    List<FriendsGroup> getAllFriendsGroups(Long ownerId);

    List<User> getAllFriends(Long userId);

    List<FriendsGroup> getGroupsByOwnerAndFriend(Long ownerId, User friend);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getAllWithoutCurrentUser(Long userId);

    boolean areFriends(Long userId, User friend);

    Map<User, List<FriendsGroup>> getFriendsGroupsMap(Long ownerId);

    Long createUser(String firstName, String lastName, String login, String password);

    void removeFromFriends(User friend);

    void update(List<User> users);

    Set<User> getSetByIds(Long[] usersIds);
}
