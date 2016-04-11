package com.geekhub.service;

import com.geekhub.entity.Event;
import com.geekhub.entity.User;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface EventService extends EntityService<Event, Long> {

    void sendEvent(Set<User> recipients, String text, User sender);

    void sendEvent(Set<User> recipients, String text, String link, User sender);
}
