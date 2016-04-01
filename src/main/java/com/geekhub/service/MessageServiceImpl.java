package com.geekhub.service;

import com.geekhub.dao.MessageDao;
import com.geekhub.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired private MessageDao messageDao;

    @Override
    public List<Message> getAll(String orderParameter) {
        return messageDao.getAll(orderParameter);
    }

    @Override
    public Message getById(Long id) {
        return messageDao.getById(id);
    }

    @Override
    public Message get(String propertyName, Object value) {
        return messageDao.get(propertyName, value);
    }

    @Override
    public Long save(Message entity) {
        return messageDao.save(entity);
    }

    @Override
    public void update(Message entity) {
        messageDao.update(entity);
    }

    @Override
    public void delete(Message entity) {
        messageDao.delete(entity);
    }

    @Override
    public void delete(Long entityId) {
        messageDao.deleteById(entityId);
    }
}
