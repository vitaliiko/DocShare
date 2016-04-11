package com.geekhub.services;

import com.geekhub.dao.EventDao;
import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import com.geekhub.entities.enums.EventStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    @Autowired
    private EventDao eventDao;

    @Override
    public List<Event> getAll(String orderParameter) {
        return eventDao.getAll(orderParameter);
    }

    @Override
    public List<Event> getAllByRecipient(User recipient) {
        return eventDao.getList("recipient", recipient);
    }

    @Override
    public Event getById(Long id) {
        return eventDao.getById(id);
    }

    @Override
    public Event get(String propertyName, Object value) {
        return eventDao.get(propertyName, value);
    }

    @Override
    public Long save(Event entity) {
        return eventDao.save(entity);
    }

    @Override
    public void update(Event entity) {
        eventDao.update(entity);
    }

    @Override
    public void delete(Event entity) {
        eventDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        eventDao.deleteById(entityId);
    }

    @Override
    public List<Event> getUnread(User recipient) {
        return eventDao.getList(recipient, "eventStatus", EventStatus.UNREAD);
    }

    @Override
    public void makeRead(Collection<Event> events) {
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
}
