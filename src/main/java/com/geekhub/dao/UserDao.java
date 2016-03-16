package com.geekhub.dao;

import com.geekhub.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserDao implements EntityDao<User, Long> {

    @Autowired
    private SessionFactory sessionFactory;
    private Class<User> clazz = User.class;

    @Override
    public List<User> getAll(String orderParameter) throws HibernateException {
        return (List<User>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public User getById(Long id) throws HibernateException {
        return (User) sessionFactory.getCurrentSession()
                .get(clazz, id);
    }

    @Override
    public User get(String propertyName, Object value) throws HibernateException {
        List<User> list = (List<User>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Long save(User entity) throws HibernateException {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(User entity) throws HibernateException {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void delete(User entity) throws HibernateException {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void delete(Long entityId) throws HibernateException {
        User user = (User) sessionFactory.getCurrentSession()
                .get(clazz, entityId);

        sessionFactory.getCurrentSession()
                .delete(user);
    }
}
