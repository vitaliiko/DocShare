package com.geekhub.model;

import com.geekhub.util.DataBaseException;
import com.geekhub.util.HibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class UserDao extends EntityDaoImpl<User, Integer> {

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

    public Integer saveUser(User user) {
        hibernateUtil.openSessionAndBeginTransaction();
        Integer id = saveEntity(user);
        hibernateUtil.commitAndCloseSession();
        return id;
    }

    public void saveUsers(Collection<User> users) {
        hibernateUtil.openSessionAndBeginTransaction();
        users.forEach(this::saveUser);
        hibernateUtil.commitAndCloseSession();
    }

    public void updateUser(User user) {
        hibernateUtil.openSessionAndBeginTransaction();
        updateEntity(user);
        hibernateUtil.commitAndCloseSession();
    }

    public void deleteUser(Integer userId) throws DataBaseException {
        hibernateUtil.openSessionAndBeginTransaction();
        User user = getEntityById(User.class, userId);
        deleteEntity(User.class, user.getId());
        hibernateUtil.commitAndCloseSession();
    }
}
