package com.geekhub.service;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.Message;
import com.geekhub.entity.User;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface UserService extends EntityService<User, Long> {

    User getByLogin(String login) throws HibernateException;

    void addMessage(Long userId, Message message) throws HibernateException;

    void deleteMessage(Long userId, Long messageId) throws HibernateException;

    Set<User> getFriends(Long userId) throws HibernateException;

    FriendsGroup getFriendsGroup(Long userId, String groupName) throws HibernateException;

    void addFriendsGroup(Long userId, String groupName) throws HibernateException;

    Set<FriendsGroup> getForeignGroups(Long userId) throws HibernateException;
}
