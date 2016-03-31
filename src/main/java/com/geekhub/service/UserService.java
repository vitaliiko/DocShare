package com.geekhub.service;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.Message;
import com.geekhub.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface UserService extends EntityService<User, Long> {

    User getByLogin(String login);

    void addMessage(Long userId, Message message);

    void deleteMessage(Long userId, Long messageId);

    Set<User> getFriends(Long userId);

    FriendsGroup getFriendsGroup(Long userId, String groupName);

    void addFriendsGroup(Long userId, FriendsGroup group);

    List<FriendsGroup> getFriendsGroups(Long userId);

    List<FriendsGroup> getGroupsByOwnerAndFriend(Long ownerId, User friend);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}
