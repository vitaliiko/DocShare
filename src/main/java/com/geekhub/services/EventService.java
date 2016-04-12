package com.geekhub.services;

import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface EventService extends EntityService<Event, Long> {

    void save(List<Event> events);

    List<Event> getUnread(User recipient);

    void makeRead(Collection<Event> events);

    List<Event> getAllByRecipient(User recipient);
}