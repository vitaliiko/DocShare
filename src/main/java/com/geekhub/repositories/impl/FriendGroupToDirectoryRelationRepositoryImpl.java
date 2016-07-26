package com.geekhub.repositories.impl;

import com.geekhub.entities.FriendGroupToDirectoryRelation;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
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
    public void deleteAllByDirectory(UserDirectory directory) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FriendGroupToDirectoryRelation r WHERE r.directory = :directory")
                .setParameter("directory", directory)
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

    @Override
    public List<FriendsGroup> getAllGroupsByDirectoryIdAndRelation(UserDirectory directory, FileRelationType relationType) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("directory", directory))
                .add(Restrictions.eq("fileRelationType", relationType))
                .setProjection(Projections.property("friendsGroup"))
                .list();
    }

    @Override
    public List<FileRelationType> getAllRelationsByDirectoryIdAndUser(Long directoryId, User user) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT rel.fileRelationType FROM FriendGroupToDirectoryRelation rel " +
                             "WHERE rel.directory.id = :dirId AND :user IN ELEMENTS(rel.friendsGroup.friends)")
                .setParameter("dirId", directoryId)
                .setParameter("user", user)
                .list();
    }

    @Override
    public List<UserDirectory> getAllAccessibleDirectories(User user) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT rel.directory FROM FriendGroupToDirectoryRelation rel " +
                             "WHERE :user IN ELEMENTS(rel.friendsGroup.friends)")
                .setParameter("user", user)
                .list();
    }
}
