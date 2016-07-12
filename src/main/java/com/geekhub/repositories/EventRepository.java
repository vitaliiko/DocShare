package com.geekhub.repositories;

import com.geekhub.entities.Event;
import com.geekhub.entities.User;

import java.util.List;

public interface EventRepository extends EntityRepository<Event, Long> {

    List<Event> getList(User recipient, String propertyName, Object value);

    Long getCount(Long recipientId, String propertyName, Object value);
}
