package com.geekhub.service;

import com.geekhub.dao.EventDao;
import com.geekhub.entity.Event;
import com.geekhub.entity.User;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
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
    public void sendEvent(Set<User> recipients, String text, User sender) {
        recipients.forEach(r -> {
            Event event = new Event();
            event.setText(text);
            event.setDate(Calendar.getInstance().getTime());
            event.setSenderId(sender.getId());
            event.setSenderName(sender.toString());
            event.setRecipient(r);
            save(event);
        });

    }
}
