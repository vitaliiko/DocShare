package com.geekhub.services.impl;

import com.geekhub.repositories.EventRepository;
import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import com.geekhub.entities.enums.EventStatus;
import java.util.Collection;
import java.util.List;

import com.geekhub.services.EventService;
import com.geekhub.services.UserService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    @Inject
    private EventRepository eventRepository;

    @Inject
    private UserService userService;

    @Override
    public List<Event> getAll(String orderParameter) {
        return eventRepository.getAll(orderParameter);
    }

    @Override
    public List<Event> getAllByRecipient(User recipient) {
        return eventRepository.getList("recipient", recipient);
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.getById(id);
    }

    @Override
    public Event get(String propertyName, Object value) {
        return eventRepository.get(propertyName, value);
    }

    @Override
    public Long save(Event entity) {
        return eventRepository.save(entity);
    }

    @Override
    public void update(Event entity) {
        eventRepository.update(entity);
    }

    @Override
    public void delete(Event entity) {
        eventRepository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        eventRepository.deleteById(entityId);
    }

    @Override
    public Long getUnreadCount(Long recipientId) {
        return eventRepository.getCount(recipientId, "eventStatus", EventStatus.UNREAD);
    }

    @Override
    public void setReadStatus(Collection<Event> events) {
        if (events != null) {
            events.forEach(e -> {
                e.setEventStatus(EventStatus.READ);
                update(e);
            });
        }
    }

    @Override
    public void save(List<Event> events) {
        events.forEach(this::save);
    }

    @Override
    public Event getByHashName(String eventHashName) {
        return eventRepository.get("hashName", eventHashName);
    }

    @Override
    public void clearEvents(Long userId) {
        User user = userService.getById(userId);
        user.getEvents().clear();
        userService.update(user);
    }
}
