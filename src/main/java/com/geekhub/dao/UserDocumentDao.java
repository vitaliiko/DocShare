package com.geekhub.dao;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentStatus;
import java.util.Map;
import org.hibernate.Hibernate;
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

    public <T> List<UserDocument> getAll(String propertyName, List<T> values) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.in(propertyName, values))
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

    @Override
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

    public List<UserDocument> getList(Map<String, Object> propertiesMap) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .list();
    }

    public UserDocument get(User owner, String propertyName, Object value) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    public UserDocument get(Map<String, Object> propertiesMap) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .uniqueResult();
    }

    public UserDocument getWithOldVersions(Long docId) {
        UserDocument document = getById(docId);
        Hibernate.initialize(document.getDocumentOldVersions());
        return document;
    }

    public List<UserDocument> getByReader(User reader) {
        return sessionFactory.getCurrentSession()
                .createQuery("from UserDocument doc " +
                        "where doc.documentStatus = :status and :reader in elements(doc.readers)")
                .setParameter("reader", reader)
                .setParameter("status", DocumentStatus.ACTUAL)
                .list();
    }

    public List<UserDocument> getByEditor(User editor) {
        return sessionFactory.getCurrentSession()
                .createQuery("from UserDocument doc " +
                        "where doc.documentStatus = :status and :editor in elements(doc.editors)")
                .setParameter("editor", editor)
                .setParameter("status", DocumentStatus.ACTUAL)
                .list();
    }

    public List<UserDocument> getByReadersGroup(FriendsGroup readersGroup) {
        return sessionFactory.getCurrentSession()
                .createQuery("from UserDocument doc " +
                        "where doc.documentStatus = :status and :readersGroup in elements(doc.readersGroups)")
                .setParameter("readersGroup", readersGroup)
                .setParameter("status", DocumentStatus.ACTUAL)
                .list();
    }

    public List<UserDocument> getByEditorsGroup(FriendsGroup editorsGroup) {
        return sessionFactory.getCurrentSession()
                .createQuery("from UserDocument doc " +
                        "where doc.documentStatus = :status and :editorsGroup in elements(doc.editorsGroups)")
                .setParameter("editorsGroup", editorsGroup)
                .setParameter("status", DocumentStatus.ACTUAL)
                .list();
    }

    public List<UserDocument> search(User owner, String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.like(propertyName, "%" + value + "%"))
                .list();
    }
}
