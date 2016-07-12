package com.geekhub.repositories;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.DocumentStatus;
import java.util.List;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class UserDirectoryRepository implements EntityRepository<UserDirectory, Long> {

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

    public List<UserDirectory> getList(User owner, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    public List<UserDirectory> getList(Map<String, Object> propertiesMap) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .list();
    }

    public List<Object> getPropertiesList(String selectProperty, String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(this.clazz)
                .add(Restrictions.eq(propertyName, value))
                .setProjection(Projections.property(selectProperty))
                .list();
    }

    public List<UserDirectory> getLike(String parentDirectoryHash, String dirName) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("parentDirectoryHash", parentDirectoryHash))
                .add(Restrictions.like("name", dirName, MatchMode.START))
                .list();
    }

    public UserDirectory get(User owner, String propertyName, Object value) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    public UserDirectory get(Map<String, Object> propertiesMap) {
        return (UserDirectory) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.allEq(propertiesMap))
                .uniqueResult();
    }

    public Long getCountByReadersGroup(FriendsGroup readersGroup) {
        return (Long) sessionFactory.getCurrentSession()
                .createQuery("select count(*) from UserDirectory dir " +
                        "where dir.documentStatus = :status and :readersGroup in elements(dir.readersGroups)")
                .setParameter("readersGroup", readersGroup)
                .setParameter("status", DocumentStatus.ACTUAL)
                .uniqueResult();
    }

    public List<UserDirectory> search(User owner, String propertyName, String value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.like(propertyName, "%" + value + "%"))
                .list();
    }
}
