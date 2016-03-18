package com.geekhub.service;

import com.geekhub.dao.UserDao;
import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.Message;
import com.geekhub.entity.User;
import org.hibernate.HibernateException;
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
    public List<User> getAll(String orderParameter) throws HibernateException {
        return userDao.getAll(orderParameter);
    }

    @Override
    public User getById(Long id) throws HibernateException {
        return userDao.getById(id);
    }

    @Override
    public User get(String propertyName, Object value) throws HibernateException {
        return userDao.get(propertyName, value);
    }

    @Override
    public Long save(User entity) throws HibernateException {
        return userDao.save(entity);
    }

    @Override
    public void update(User entity) throws HibernateException {
        userDao.update(entity);
    }

    @Override
    public void delete(User entity) throws HibernateException {
        userDao.delete(entity);
    }

    @Override
    public void delete(Long entityId) throws HibernateException {
        userDao.delete(entityId);
    }

    @Override
    public User getByLogin(String login) throws HibernateException {
        return userDao.get("login", login);
    }

    @Override
    public void addMessage(Long userId, Message message) throws HibernateException {
        User user = userDao.getById(userId);
        user.getMessageSet().add(message);
        userDao.update(user);
    }

    @Override
    public void deleteMessage(Long userId, Long messageId) throws HibernateException {
        Message message = messageService.getById(messageId);
        User user = userDao.getById(userId);
        user.getMessageSet().remove(message);
        userDao.update(user);
    }

    @Override
    public Set<User> getFriends(Long userId) throws HibernateException {
        return userDao.getFriends(userId);
    }

    @Override
    public FriendsGroup getFriendsGroup(Long userId, String groupName) throws HibernateException {
        return userDao.getFriendsGroup(userId, groupName);
    }
}
