package com.geekhub.dao;

import com.geekhub.entity.Message;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class MessageDao implements EntityDao<Message, Long> {

    @Autowired
    private SessionFactory sessionFactory;
    private Class<Message> clazz = Message.class;

    @Override
    public List<Message> getAll(String orderParameter) {
        return (List<Message>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public Message getById(Long id) {
        return (Message) sessionFactory.getCurrentSession()
                .get(clazz, id);
    }

    @Override
    public Message get(String propertyName, Object value) {
        return (Message) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(Message entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(Message entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(Message entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(Message entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        Message message = getById(entityId);
        sessionFactory.getCurrentSession().delete(message);
    }
}
