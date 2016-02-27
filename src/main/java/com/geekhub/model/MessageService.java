package com.geekhub.model;

import com.geekhub.util.DataBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired private MessageDao messageDao;

    public List<Message> getMessages() throws DataBaseException {
        return messageDao.getAllEntities(Message.class, "date");
    }

    public Message getMessageById(Integer messageId) throws DataBaseException {
        return messageDao.getEntityById(Message.class, messageId);
    }
}
