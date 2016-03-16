package com.geekhub.service;

import com.geekhub.dao.MessageDao;
import com.geekhub.entity.Message;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired private MessageDao messageDao;

    @Override
    public List<Message> getAll(String orderParameter) throws HibernateException {
        return messageDao.getAll(orderParameter);
    }

    @Override
    public Message getById(Long id) throws HibernateException {
        return messageDao.getById(id);
    }

    @Override
    public Message get(String propertyName, Object value) throws HibernateException {
        return messageDao.get(propertyName, value);
    }

    @Override
    public Long save(Message entity) throws HibernateException {
        return messageDao.save(entity);
    }

    @Override
    public void update(Message entity) throws HibernateException {
        messageDao.update(entity);
    }

    @Override
    public void delete(Message entity) throws HibernateException {
        messageDao.delete(entity);
    }

    @Override
    public void delete(Long entityId) throws HibernateException {
        messageDao.delete(entityId);
    }
}
