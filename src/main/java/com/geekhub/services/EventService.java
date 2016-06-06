package com.geekhub.services;

import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface EventService extends EntityService<Event, Long> {

    void save(List<Event> events);

    Long getUnreadCount(Long recipientId);

    void setReadStatus(Collection<Event> events);

    List<Event> getAllByRecipient(User recipient);

    Event getByHashName(String eventHashName);

    void clearEvents(Long userId);
}
