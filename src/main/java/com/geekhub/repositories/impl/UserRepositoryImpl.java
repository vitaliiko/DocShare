package com.geekhub.repositories.impl;

import com.geekhub.entities.User;
import java.util.Map;

import com.geekhub.repositories.UserRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserRepositoryImpl implements UserRepository {

    @Inject
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

    // TODO: 15.07.2016 with criteria or hql throw ClassCastException (trying cast User to Long, don't know why)
    @Override
    public List<User> getByIds(List<Long> userIds) {
        String sql = "SELECT * FROM User u WHERE u.id IN (";
        for (int i = 1; i <= userIds.size(); i++) {
            sql += "?" + (i == userIds.size() ? ")" : ", ");
        }
        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.addEntity(User.class);
        for (int i = 0; i < userIds.size(); i++) {
            query.setParameter(i, userIds.get(i));
        }
        return query.list();
    }

    @Override
    public List<User> getByFriend(User friend) {
        return sessionFactory.getCurrentSession()
                .createQuery("from User u where :friend in elements(u.friends)")
                .setParameter("friend", friend)
                .list();
    }

    @Override
    public List<User> search(String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.like(propertyName, value, MatchMode.ANYWHERE).ignoreCase())
                .list();
    }

    @Override
    public List<User> search(String propertyName, String value, Map<String, String> searchingMap) {
        Criteria criteria = sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.or(Restrictions.like(propertyName, value, MatchMode.ANYWHERE).ignoreCase()));
        searchingMap.forEach((k, v) -> criteria
                .add(Restrictions.or(Restrictions.like(k, v, MatchMode.ANYWHERE).ignoreCase())));
        return criteria.list();
    }
 }
