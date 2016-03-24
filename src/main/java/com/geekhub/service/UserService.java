package com.geekhub.service;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.Message;
import com.geekhub.entity.User;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface UserService extends EntityService<User, Long> {

    User getByLogin(String login) throws HibernateException;

    void addMessage(Long userId, Message message) throws HibernateException;

    void deleteMessage(Long userId, Long messageId) throws HibernateException;

    Set<User> getFriends(Long userId) throws HibernateException;

    FriendsGroup getFriendsGroup(Long userId, String groupName) throws HibernateException;

    void addFriendsGroup(Long userId, String groupName) throws HibernateException;

    void addFriendsGroup(Long userId, String groupName, List<Long> friendsIds) throws HibernateException;

    List<FriendsGroup> getFriendsGroups(Long userId) throws HibernateException;

    List<FriendsGroup> getByOwnerAndFriend(Long ownerId, User friend) throws HibernateException;

    void addFriend(Long userId, Long friendId) throws HibernateException;

    void deleteFriend(Long userId, Long friendId) throws HibernateException;
}
