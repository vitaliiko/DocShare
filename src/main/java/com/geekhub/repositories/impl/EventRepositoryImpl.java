package com.geekhub.repositories.impl;

import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import java.util.List;

import com.geekhub.repositories.EventRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepositoryImpl implements EventRepository {

    @Inject
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
    public List<Event> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public List<Event> getList(User recipient, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("recipient", recipient))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long getCount(Long recipientId, String propertyName, Object value) {
        return (Long) sessionFactory.getCurrentSession()
                .createCriteria(clazz, "event")
                .createAlias("event.recipient", "rec")
                .add(Restrictions.eq("rec.id", recipientId))
                .add(Restrictions.eq(propertyName, value))
                .setProjection(Projections.rowCount())
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
