package com.geekhub.dao;

import com.geekhub.entities.RemovedDirectory;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class RemovedDirectoryDao implements EntityDao<RemovedDirectory, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<RemovedDirectory> clazz = RemovedDirectory.class;

    @Override
    public List<RemovedDirectory> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public RemovedDirectory getById(Long id) {
        return (RemovedDirectory) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public RemovedDirectory get(String propertyName, Object value) {
        return (RemovedDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<RemovedDirectory> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(RemovedDirectory entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(RemovedDirectory entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(RemovedDirectory entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(RemovedDirectory entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        RemovedDirectory RemovedDirectory = getById(entityId);
        sessionFactory.getCurrentSession().delete(RemovedDirectory);
    }
}
