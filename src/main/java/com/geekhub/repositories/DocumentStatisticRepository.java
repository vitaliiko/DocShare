package com.geekhub.repositories;

import com.geekhub.entities.DocumentStatistic;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class DocumentStatisticRepository implements EntityRepository<DocumentStatistic, Long> {

    @Inject
    private SessionFactory sessionFactory;

    private Class<DocumentStatistic> clazz = DocumentStatistic.class;

    @Override
    public List<DocumentStatistic> getAll(String orderParameter) {
        return (List<DocumentStatistic>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public DocumentStatistic getById(Long id) {
        return (DocumentStatistic) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public DocumentStatistic get(String propertyName, Object value) {
        return (DocumentStatistic) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<DocumentStatistic> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(DocumentStatistic entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(DocumentStatistic entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(DocumentStatistic entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(DocumentStatistic entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        DocumentStatistic comment = getById(entityId);
        sessionFactory.getCurrentSession().update(comment);
    }

    public DocumentStatistic getByUserDocumentId(Long documentId) {
        return (DocumentStatistic) sessionFactory.getCurrentSession()
                .createCriteria(clazz, "stat")
                .createAlias("stat.userDocument", "doc")
                .add(Restrictions.eq("doc.id", documentId))
                .uniqueResult();
    }
}
