package com.geekhub.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class EntityDaoImpl<T extends MappedEntity> implements EntityDao<T> {

    @Autowired private SessionFactory sessionFactory;

    @Override
    public List<T> getAllEntities(Class<T> clazz, String orderParameter) {
        Session session = sessionFactory.openSession();
        try {
            return (List<T>) session.createCriteria(clazz)
                    .addOrder(Order.asc(orderParameter))
                    .list();
        } finally {
            session.close();
        }
    }

    @Override
    public T getEntityById(Class<T> clazz, long id) {
        Session session = sessionFactory.openSession();
        try {
            return (T) sessionFactory.openSession().load(clazz, id);
        } finally {
            session.close();
        }
    }

    @Override
    public T getEntity(Class<T> clazz, String propertyName, Object value) {
        Session session = sessionFactory.openSession();
        try {
            List<T> list = (List<T>) session.createCriteria(clazz)
                    .add(Restrictions.eq(propertyName, value))
                    .list();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public void saveEntity(T entity) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void updateEntity(T entity) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteEntity(T entity) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
