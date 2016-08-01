package com.geekhub.repositories.impl;

import com.geekhub.entities.FileSharedLinkToken;
import com.geekhub.repositories.FileSharedLinkTokenRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
public class FileSharedLinkTokenRepositoryImpl implements FileSharedLinkTokenRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<FileSharedLinkToken> clazz = FileSharedLinkToken.class;

    @Override
    public List<FileSharedLinkToken> getAll(String orderParameter) {
        return (List<FileSharedLinkToken>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FileSharedLinkToken getById(Long id) {
        return (FileSharedLinkToken) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public FileSharedLinkToken get(String propertyName, Object value) {
        return (FileSharedLinkToken) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<FileSharedLinkToken> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(FileSharedLinkToken entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(FileSharedLinkToken entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(FileSharedLinkToken entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(FileSharedLinkToken entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        sessionFactory.getCurrentSession().delete(getById(entityId));
    }
}
