package com.geekhub.service;

import com.geekhub.dao.EventDao;
import com.geekhub.entity.Event;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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
}
