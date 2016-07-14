package com.geekhub.repositories.impl;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserToDirectoryRelation;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserToDirectoryRelationRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Repository
@SuppressWarnings("unchecked")
public class UserToDirectoryRelationRepositoryImpl implements UserToDirectoryRelationRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<UserToDirectoryRelation> clazz = UserToDirectoryRelation.class;

    @Override
    public List<UserToDirectoryRelation> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public UserToDirectoryRelation getById(Long id) {
        return (UserToDirectoryRelation) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public UserToDirectoryRelation get(String propertyName, Object value) {
        return (UserToDirectoryRelation) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(UserToDirectoryRelation entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(UserToDirectoryRelation entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(UserToDirectoryRelation entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(UserToDirectoryRelation entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        UserToDirectoryRelation userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    @Override
    public List<UserToDirectoryRelation> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public void deleteByDirectoryBesidesOwner(UserDirectory directory) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE UserToDirectoryRelation r WHERE r.directory = :directory AND r.fileRelationType != :relation")
                .setParameter("directory", directory)
                .setParameter("relation", FileRelationType.OWNER)
                .executeUpdate();
    }

    @Override
    public List<UserDirectory> getAllAccessibleDirectories(User user) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.ne("relationType", FileRelationType.OWNER))
                .setProjection(Projections.property("directory"))
                .list();
    }
}
