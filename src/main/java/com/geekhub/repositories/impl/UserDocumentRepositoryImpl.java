package com.geekhub.repositories.impl;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;

import java.util.Iterator;
import java.util.Map;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserDocumentRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserDocumentRepositoryImpl implements UserDocumentRepository {

    @Inject
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

    @Override
    public List<UserDocument> getList(User owner, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public List<UserDocument> getList(Map<String, Object> propertiesMap) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .list();
    }

    @Override
    public <T> List<UserDocument> getList(String propertyName, List<T> values) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.in(propertyName, values))
                .list();
    }

    @Override
    public List<Object> getPropertiesList(String selectProperty, String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(this.clazz)
                .add(Restrictions.eq(propertyName, value))
                .setProjection(Projections.property(selectProperty))
                .list();
    }

    @Override
    public UserDocument get(User owner, String propertyName, Object value) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<String> getSimilarDocumentNamesInDirectory(String directoryHash, String pattern) {
        String sql = "SELECT d.name FROM user_document d WHERE d.parentDirectoryHash = ? AND d.name REGEXP " + pattern;
        return sessionFactory.getCurrentSession()
                .createSQLQuery(sql)
                .setParameter(0, directoryHash)
                .list();
    }

    @Override
    public UserDocument get(Map<String, Object> propertiesMap) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .uniqueResult();
    }

    @Override
    public UserDocument getByFullNameAndOwner(Map<String, Object> propertiesMap) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createQuery("SELECT doc FROM UserToDocumentRelation rel JOIN rel.document doc " +
                             "WHERE doc.parentDirectoryHash = :parentDirHash AND doc.name = :name AND rel.user = :owner " +
                             "AND rel.fileRelationType = :relation")
                .setParameter("parentDirHash", propertiesMap.get("parentDirectoryHash"))
                .setParameter("name", propertiesMap.get("name"))
                .setParameter("owner", propertiesMap.get("owner"))
                .setParameter("relation", FileRelationType.OWN)
                .uniqueResult();
    }

    @Override
    public List<UserDocument> getAllByUserAndRelationType(User user, FileRelationType relation) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT doc FROM UserToDocumentRelation rel JOIN rel.document doc " +
                             "WHERE rel.user = :user AND rel.fileRelationType = :relation")
                .setParameter("user", user)
                .setParameter("relation", relation)
                .list();
    }

    @Override
    public List<UserDocument> getAllByFriendGroupAndRelationType(FriendsGroup group, FileRelationType relation) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT doc FROM FriendGroupToDocumentRelation rel JOIN rel.document doc " +
                             "WHERE rel.friendsGroup = :group AND rel.fileRelationType = :relation")
                .setParameter("group", group)
                .setParameter("relation", relation)
                .list();
    }

    @Override
    public List<UserDocument> search(User owner, String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.like(propertyName, "%" + value + "%"))
                .list();
    }

    @Override
    public void updateDocumentAttribute(DocumentAttribute attribute, List<Long> documentIds) {
        sessionFactory.getCurrentSession()
                .createQuery("UPDATE UserDocument d SET d.documentAttribute = :attribute WHERE d.id IN :ids")
                .setParameter("attribute", attribute)
                .setParameterList("ids", documentIds)
                .executeUpdate();
    }
}
