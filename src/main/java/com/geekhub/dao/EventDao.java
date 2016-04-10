package com.geekhub.dao;

import com.geekhub.entity.Event;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EventDao implements EntityDao<Event, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<Event> clazz = Event.class;

    @Override
    public List<Event> getAll(String orderParameter) {
        return (List<Event>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public Event getById(Long id) {
        return (Event) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public Event get(String propertyName, Object value) {
        return (Event) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(Event entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(Event entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(Event entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(Event entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        sessionFactory.getCurrentSession().delete(getById(entityId));
    }
}