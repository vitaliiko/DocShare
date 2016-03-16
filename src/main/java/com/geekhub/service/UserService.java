package com.geekhub.service;

import com.geekhub.entity.Message;
import com.geekhub.entity.User;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends EntityService<User, Long> {

    User getByLogin(String login) throws HibernateException;

    void addMessage(Long userId, Message message) throws HibernateException;

    void deleteMessage(Long userId, Long messageId) throws HibernateException;
}
