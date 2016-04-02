package com.geekhub.dao;

import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserDocumentDao implements EntityDao<UserDocument, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<UserDocument> clazz = UserDocument.class;

    @Override
    public List<UserDocument> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public UserDocument getById(Long id) {
        return (UserDocument) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public UserDocument get(String propertyName, Object value) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(UserDocument entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(UserDocument entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(UserDocument entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(UserDocument entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        UserDocument userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    public List<UserDocument> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    public List<UserDocument> getList(User owner, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    public UserDocument get(User owner, String propertyName, Object value) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }
}
