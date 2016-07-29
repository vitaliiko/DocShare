package com.geekhub.repositories.impl;

import com.geekhub.entities.FileSharedLink;
import com.geekhub.repositories.FileSharedLinkRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
public class FileSharedLinkRepositoryImpl implements FileSharedLinkRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<FileSharedLink> clazz = FileSharedLink.class;

    @Override
    public List<FileSharedLink> getAll(String orderParameter) {
        return (List<FileSharedLink>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FileSharedLink getById(Long id) {
        return (FileSharedLink) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public FileSharedLink get(String propertyName, Object value) {
        return (FileSharedLink) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<FileSharedLink> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(FileSharedLink entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(FileSharedLink entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(FileSharedLink entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(FileSharedLink entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        sessionFactory.getCurrentSession().delete(getById(entityId));
    }
}
