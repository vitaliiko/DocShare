package com.geekhub.services;

import com.geekhub.dto.SearchDto;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface UserService extends EntityService<User, Long> {

    List<User> getByIds(List<Long> userIds);

    User getByLogin(String login);

    Set<User> getFriends(Long userId);

    FriendsGroup getFriendsGroupByName(Long ownerId, String groupName);

    void addFriendsGroup(Long ownerId, FriendsGroup group);

    List<FriendsGroup> getAllFriendsGroups(Long ownerId);

    List<User> getAllFriends(Long userId);

    List<FriendsGroup> getGroupsByOwnerAndFriend(Long ownerId, User friend);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    boolean areFriends(Long userId, User friend);

    Map<User, List<FriendsGroup>> getFriendsGroupsMap(Long ownerId);

    void removeFromFriends(User friend);

    void update(List<User> users);

    Set<User> searchByName(String name);

    Set<User> search(SearchDto searchDto);
}
