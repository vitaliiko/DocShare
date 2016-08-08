package com.geekhub.repositories.impl;

import com.geekhub.entities.UserDocumentStatistic;
import com.geekhub.repositories.UserDocumentStatisticRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserDocumentStatisticRepositoryImpl implements UserDocumentStatisticRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<UserDocumentStatistic> clazz = UserDocumentStatistic.class;

    @Override
    public List<UserDocumentStatistic> getAll(String orderParameter) {
        return (List<UserDocumentStatistic>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public UserDocumentStatistic getById(Long id) {
        return (UserDocumentStatistic) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public UserDocumentStatistic get(String propertyName, Object value) {
        return (UserDocumentStatistic) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<UserDocumentStatistic> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(UserDocumentStatistic entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(UserDocumentStatistic entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(UserDocumentStatistic entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(UserDocumentStatistic entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        UserDocumentStatistic comment = getById(entityId);
        sessionFactory.getCurrentSession().update(comment);
    }

    @Override
    public UserDocumentStatistic getByUserDocumentId(Long documentId) {
        return (UserDocumentStatistic) sessionFactory.getCurrentSession()
                .createCriteria(clazz, "stat")
                .createAlias("stat.userDocument", "doc")
                .add(Restrictions.eq("doc.id", documentId))
                .uniqueResult();
    }
}
