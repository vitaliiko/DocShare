package com.geekhub.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired private MessageDao messageDao;

    public List<Message> getMessages() {
        return messageDao.getAllEntities(Message.class, "date");
    }

    public void saveMessage(Message message) {
        messageDao.saveEntity(message);
    }

    public void deleteMessage(Integer id) {
        messageDao.deleteEntity(Message.class, id);
    }
}
