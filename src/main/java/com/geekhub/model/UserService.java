package com.geekhub.model;

import com.geekhub.util.DataBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    @Autowired UserDao userDao;

    public List<User> getUsers() throws DataBaseException {
        return userDao.getAllEntities(User.class, "lastName");
    }

    public User getUserById(int id) throws DataBaseException {
        return userDao.getEntityById(User.class, id);
    }

    public User getUserByLogin(String login) throws DataBaseException {
        return userDao.getEntity(User.class, "login", login);
    }

    public Integer saveUser(User user) throws DataBaseException {
        return userDao.saveUser(user);
    }

    public void saveUsers(Collection<User> users) throws DataBaseException {
        userDao.saveUsers(users);
    }

    public void updateUser(User user) throws DataBaseException {
        userDao.updateUser(user);
    }

    public void deleteUser(Integer userId) throws DataBaseException {
        userDao.deleteUser(userId);
    }

    public void addMessage(Integer userId, Message message) throws DataBaseException {
        userDao.addMessage(userId, message);
    }

    public void deleteMessage(Integer userId, Integer messageId) throws DataBaseException {
        userDao.deleteMessage(userId, messageId);
    }
}
