package com.geekhub.dao;

import com.geekhub.entities.FriendGroupToDocumentRelation;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class FriendGroupToDocumentRelationDao implements EntityDao<FriendGroupToDocumentRelation, Long> {

    @Inject
    private SessionFactory sessionFactory;

    private Class<FriendGroupToDocumentRelation> clazz = FriendGroupToDocumentRelation.class;

    @Override
    public List<FriendGroupToDocumentRelation> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FriendGroupToDocumentRelation getById(Long id) {
        return (FriendGroupToDocumentRelation) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public FriendGroupToDocumentRelation get(String propertyName, Object value) {
        return (FriendGroupToDocumentRelation) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(FriendGroupToDocumentRelation entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(FriendGroupToDocumentRelation entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(FriendGroupToDocumentRelation entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(FriendGroupToDocumentRelation entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        FriendGroupToDocumentRelation userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    @Override
    public List<FriendGroupToDocumentRelation> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }
}
