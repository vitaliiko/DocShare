package com.geekhub.dao;

import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.enums.DocumentAttribute;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class UserDirectoryDao implements EntityDao<UserDirectory, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<UserDirectory> clazz = UserDirectory.class;
    private SimpleExpression notRoot = Restrictions.ne("documentAttribute", DocumentAttribute.ROOT);

    @Override
    public List<UserDirectory> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(notRoot)
                .addOrder(Order.asc(orderParameter))
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

    public List<UserDirectory> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .add(notRoot)
                .list();
    }

    public List<UserDirectory> getList(User owner, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .add(notRoot)
                .list();
    }

    public UserDirectory get(User owner, String propertyName, Object value) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .add(notRoot)
                .uniqueResult();
    }

    public UserDirectory get(Map<String, Object> propertiesMap) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .add(notRoot)
                .uniqueResult();
    }
}
