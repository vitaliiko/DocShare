package com.geekhub.repositories.impl;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.FriendGroupToDirectoryRelationRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class FriendGroupToDirectoryRelationRepositoryImpl implements FriendGroupToDirectoryRelationRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<FriendGroupToDirectoryRelation> clazz = FriendGroupToDirectoryRelation.class;

    @Override
    public List<FriendGroupToDirectoryRelation> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FriendGroupToDirectoryRelation getById(Long id) {
        return (FriendGroupToDirectoryRelation) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public FriendGroupToDirectoryRelation get(String propertyName, Object value) {
        return (FriendGroupToDirectoryRelation) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(FriendGroupToDirectoryRelation entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(FriendGroupToDirectoryRelation entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(FriendGroupToDirectoryRelation entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(FriendGroupToDirectoryRelation entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        FriendGroupToDirectoryRelation userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    @Override
    public List<FriendGroupToDirectoryRelation> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public void deleteByDirectoryBesidesOwner(UserDirectory directory) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FriendGroupToDirectoryRelation r WHERE r.directory = :directory AND r.fileRelationType != :relation")
                .setParameter("directory", directory)
                .setParameter("relation", FileRelationType.OWNER)
                .executeUpdate();
    }

    @Override
    public Long getCountByFriendGroup(FriendsGroup group) {
        return (Long) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("friendsGroup", group))
                .setProjection(Projections.rowCount())
                .uniqueResult();
    }
}
