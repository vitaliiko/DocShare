package com.geekhub.dao;

import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class RemovedDocumentDao implements EntityDao<RemovedDocument, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<RemovedDocument> clazz = RemovedDocument.class;

    @Override
    public List<RemovedDocument> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public RemovedDocument getById(Long id) {
        return (RemovedDocument) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public RemovedDocument get(String propertyName, Object value) {
        return (RemovedDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    public RemovedDocument get(User owner, String propertyName, Object value) {
        return (RemovedDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    public RemovedDocument getByUserDocumentHashName(String userDocumentHashName) {
        return (RemovedDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rem")
                .createAlias("rem.userDocument", "doc")
                .add(Restrictions.eq("doc.hashName", userDocumentHashName))
                .uniqueResult();
    }

    @Override
    public List<RemovedDocument> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(RemovedDocument entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(RemovedDocument entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(RemovedDocument entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(RemovedDocument entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        RemovedDocument RemovedDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(RemovedDocument);
    }

    public RemovedDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name) {
        return (RemovedDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rem")
                .createAlias("rem.userDocument", "doc")
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq("doc.name", name))
                .add(Restrictions.eq("doc.parentDirectoryHash", parentDirectoryHash))
                .uniqueResult();
    }
}
