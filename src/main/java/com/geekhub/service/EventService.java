package com.geekhub.service;

import com.geekhub.entity.Event;
import com.geekhub.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface EventService extends EntityService<Event, Long> {

    void save(List<Event> events);

    List<Event> getUnread(User recipient);

    void makeRead(Collection<Event> events);

    List<Event> getAllByRecipient(User recipient);
}
