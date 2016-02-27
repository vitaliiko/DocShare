package com.geekhub.model;

import com.geekhub.util.DataBaseException;
import com.geekhub.util.HibernateUtil;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class EntityDaoImpl<T> implements EntityDao<T> {

    @Autowired private HibernateUtil hibernateUtil;

    @Override
    public List<T> getAllEntities(Class<T> clazz, String orderParameter) throws DataBaseException {
        return (List<T>) hibernateUtil.getSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public T getEntityById(Class<T> clazz, int id) throws DataBaseException {
        return (T) hibernateUtil.getSession().load(clazz, id);
    }

    @Override
    public T getEntity(Class<T> clazz, String propertyName, Object value) throws DataBaseException {
        List<T> list = (List<T>) hibernateUtil.getSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void saveEntity(T entity) throws DataBaseException {
        hibernateUtil.getSession().save(entity);
    }

    @Override
    public void updateEntity(T entity) throws DataBaseException {
        hibernateUtil.getSession().update(entity);
    }

    @Override
    public void deleteEntity(T entity) throws DataBaseException {
        hibernateUtil.getSession().delete(entity);
    }

    @Override
    public void deleteEntity(Class<T> clazz, Integer entityId) throws DataBaseException {
        T entity = (T) hibernateUtil.getSession().load(clazz, entityId);
        hibernateUtil.getSession().delete(entity);
    }
}
