package com.geekhub.model;

import com.geekhub.util.DataBaseException;
import com.geekhub.util.HibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends EntityDaoImpl<User> {

    @Autowired private HibernateUtil hibernateUtil;
    @Autowired private MessageService messageService;

    public void addMessage(Integer id, Message message) throws DataBaseException {
        hibernateUtil.openSessionAndBeginTransaction();
        User user = getEntityById(User.class, id);
        user.getMessageSet().add(message);
        updateEntity(user);
        hibernateUtil.commitAndCloseSession();
    }

    public void deleteMessage(Integer userId, Integer messageId) throws DataBaseException {
        hibernateUtil.openSessionAndBeginTransaction();
        User user = getEntityById(User.class, userId);
        Message message = messageService.getMessageById(messageId);
        user.getMessageSet().remove(message);
        updateEntity(user);
        hibernateUtil.commitAndCloseSession();
    }

    public void saveUser(Integer userId) {
        hibernateUtil.openSessionAndBeginTransaction();
        User user = getEntityById(User.class, userId);
        saveEntity(user);
        hibernateUtil.commitAndCloseSession();
    }

    public void updateUser(Integer userId) {
        hibernateUtil.openSessionAndBeginTransaction();
        User user = getEntityById(User.class, userId);
        updateEntity(user);
        hibernateUtil.commitAndCloseSession();
    }

    public void deleteUser(Integer userId) throws DataBaseException {
        hibernateUtil.openSessionAndBeginTransaction();
        User user = getEntityById(User.class, userId);
        deleteEntity(user);
        hibernateUtil.commitAndCloseSession();
    }
}
