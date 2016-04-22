package com.geekhub.dao;

import com.geekhub.entities.User;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
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
    public List<User> getAll(String orderParameter) {
        return (List<User>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public User getById(Long id) {
        return (User) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public User get(String propertyName, Object value) {
        return (User) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<User> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(User entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(User entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(User entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(User entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        User user = getById(entityId);
        sessionFactory.getCurrentSession().delete(user);
    }

    public List<User> getByFriend(User friend) {
        return sessionFactory.getCurrentSession()
                .createQuery("from User u where :friend in elements(u.friends)")
                .setParameter("friend", friend)
                .list();
    }

    public List<User> search(String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.like(propertyName, value, MatchMode.ANYWHERE).ignoreCase())
                .list();
    }

    public List<User> search(String propertyName, String value, Map<String, String> searchingMap) {
        Criteria criteria = sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.or(Restrictions.like(propertyName, value, MatchMode.ANYWHERE).ignoreCase()));
        searchingMap.forEach((k, v) -> criteria
                .add(Restrictions.or(Restrictions.like(k, v, MatchMode.ANYWHERE).ignoreCase())));
        return criteria.list();
    }
 }
