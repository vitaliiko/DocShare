package com.geekhub.service;

import com.geekhub.entity.Message;
import org.springframework.stereotype.Service;

@Service
public interface MessageService extends EntityService<Message, Long> {
}
