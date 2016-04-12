package com.geekhub.dao;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import java.util.List;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class UserDirectoryDao implements EntityDao<UserDirectory, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<UserDirectory> clazz = UserDirectory.class;

    @Override
    public List<UserDirectory> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    public <T> List<UserDirectory> getAll(String propertyName, List<T> values) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.in(propertyName, values))
                .list();
    }

    @Override
    public UserDirectory getById(Long id) {
        return (UserDirectory) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public UserDirectory get(String propertyName, Object value) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(UserDirectory entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(UserDirectory entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(UserDirectory entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(UserDirectory entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        UserDirectory userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    @Override
    public List<UserDirectory> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    public List<UserDirectory> getList(User owner, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    public List<UserDirectory> getList(Map<String, Object> propertiesMap) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .list();
    }

    public UserDirectory get(User owner, String propertyName, Object value) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    public UserDirectory get(Map<String, Object> propertiesMap) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .uniqueResult();
    }
}
