package com.geekhub.dao;

import com.geekhub.entity.Message;
import com.geekhub.service.MessageService;

import org.hibernate.HibernateException;
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
    public List<Message> getAll(String orderParameter) throws HibernateException {
        return (List<Message>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public Message getById(Long id) throws HibernateException {
        return (Message) sessionFactory.getCurrentSession()
                .get(clazz, id);
    }

    @Override
    public Message get(String propertyName, Object value) throws HibernateException {
        List<Message> list = (List<Message>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Long save(Message entity) throws HibernateException {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(Message entity) throws HibernateException {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void delete(Message entity) throws HibernateException {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void delete(Long entityId) throws HibernateException {
        Message message = (Message) sessionFactory.getCurrentSession()
                .get(clazz, entityId);

        sessionFactory.getCurrentSession()
                .delete(message);
    }
}
