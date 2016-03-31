package com.geekhub.service;

import com.geekhub.dao.UserDao;
import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.Message;
import com.geekhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageService messageService;

    @Override
    public List<User> getAll(String orderParameter) {
        return userDao.getAll(orderParameter);
    }

    @Override
    public User getById(Long id) {
        return userDao.getById(id);
    }

    @Override
    public User get(String propertyName, Object value) {
        return userDao.get(propertyName, value);
    }

    @Override
    public Long save(User entity) {
        return userDao.save(entity);
    }

    @Override
    public void update(User entity) {
        userDao.update(entity);
    }

    @Override
    public void delete(User entity) {
        userDao.delete(entity);
    }

    @Override
    public void delete(Long entityId) {
        userDao.delete(entityId);
    }

    @Override
    public User getByLogin(String login) {
        return userDao.get("login", login);
    }

    @Override
    public void addMessage(Long userId, Message message) {
        User user = userDao.getById(userId);
        user.getMessageSet().add(message);
        userDao.update(user);
    }

    @Override
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageService.getById(messageId);
        User user = userDao.getById(userId);
        user.getMessageSet().remove(message);
        userDao.update(user);
    }

    @Override
    public Set<User> getFriends(Long userId) {
        return userDao.getFriends(userId);
    }

    @Override
    public FriendsGroup getFriendsGroup(Long userId, String groupName) {
        return userDao.getFriendsGroup(userId, groupName);
    }

    @Override
    public List<FriendsGroup> getFriendsGroups(Long userId) {
        return userDao.getFriendsGroups(userId);
    }

    @Override
    public List<FriendsGroup> getGroupsByOwnerAndFriend(Long ownerId, User friend) {
        return userDao.getGroupsByOwnerAndFriend(ownerId, friend);
    }

    @Override
    public void addFriendsGroup(Long userId, FriendsGroup group) {
        userDao.addFriendsGroup(userId, group);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userDao.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userDao.deleteFriend(userId, friendId);
    }
}
