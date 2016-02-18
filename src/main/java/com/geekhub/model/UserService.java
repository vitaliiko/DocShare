package com.geekhub.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired UserDao userDao;

    public List<User> getUsers() {
        return userDao.getAllEntities(User.class, "lastName");
    }

    public User getUserById(int id) {
        return userDao.getEntityById(User.class, id);
    }

    public User getUserByLogin(String login) {
        return userDao.getEntity(User.class, "login", login);
    }

    public void saveUser(User user) {
        userDao.saveEntity(user);
    }

    public void updateUser(User user) {
        userDao.updateEntity(user);
    }

    public void deleteUser(User user) {
        userDao.deleteEntity(user);
    }

    public void addMessage(User user, Message message) {
        userDao.addMessage(user, message);
    }

    public void addFriend(User user, String friendLogin) {
        userDao.addFriend(user, friendLogin);
    }
}
