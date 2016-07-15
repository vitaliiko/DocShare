package com.geekhub.repositories.impl;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import java.util.List;
import java.util.Map;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserDirectoryRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class UserDirectoryRepositoryImpl implements UserDirectoryRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<UserDirectory> clazz = UserDirectory.class;

    @Override
    public List<UserDirectory> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public <T> List<UserDirectory> getAll(String propertyName, List<T> values) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.in(propertyName, values))
                .list();
    }

    @Override
    public UserDirectory getById(Long id) {
        return (UserDirectory) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public UserDirectory get(String propertyName, Object value) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(UserDirectory entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(UserDirectory entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(UserDirectory entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(UserDirectory entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        UserDirectory userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    @Override
    public List<UserDirectory> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public List<UserDirectory> getList(User owner, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public List<UserDirectory> getList(Map<String, Object> propertiesMap) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
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
    public List<UserDirectory> getLike(String parentDirectoryHash, String dirName) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("parentDirectoryHash", parentDirectoryHash))
                .add(Restrictions.like("name", dirName, MatchMode.START))
                .list();
    }

    @Override
    public UserDirectory get(User owner, String propertyName, Object value) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public UserDirectory getByFullNameAndOwner(Map<String, Object> propertiesMap) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createQuery("SELECT dir FROM UserToDirectoryRelation rel JOIN rel.directory dir " +
                        "WHERE dir.parentDirectoryHash = :parentDirHash AND dir.name = :name AND rel.user = :owner " +
                        "AND rel.fileRelationType = :relation")
                .setParameter("parentDirHash", propertiesMap.get("parentDirectoryHash"))
                .setParameter("name", propertiesMap.get("name"))
                .setParameter("owner", propertiesMap.get("owner"))
                .setParameter("relation", FileRelationType.OWNER)
                .uniqueResult();
    }

    @Override
    public List<UserDirectory> search(User owner, String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.like(propertyName, "%" + value + "%"))
                .list();
    }

    @Override
    public void updateDocumentAttribute(DocumentAttribute attribute, List<Long> directoryIds) {
        sessionFactory.getCurrentSession()
                .createQuery("UPDATE UserDirectory d SET d.documentAttribute = :attribute WHERE d.id IN :ids")
                .setParameter("attribute", attribute)
                .setParameterList("ids", directoryIds)
                .executeUpdate();
    }
}
