package com.geekhub.repositories;

import com.geekhub.entities.DocumentOldVersion;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class DocumentOldVersionRepository implements EntityRepository<DocumentOldVersion, Long> {

    @Inject
    private SessionFactory sessionFactory;

    private Class<DocumentOldVersion> clazz = DocumentOldVersion.class;

    @Override
    public List<DocumentOldVersion> getAll(String orderParameter) {
        return (List<DocumentOldVersion>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public DocumentOldVersion getById(Long id) {
        return (DocumentOldVersion) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public DocumentOldVersion get(String propertyName, Object value) {
        return (DocumentOldVersion) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<DocumentOldVersion> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(DocumentOldVersion entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(DocumentOldVersion entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(DocumentOldVersion entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(DocumentOldVersion entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        DocumentOldVersion friendsGroup = getById(entityId);
        sessionFactory.getCurrentSession().delete(friendsGroup);
    }
}
