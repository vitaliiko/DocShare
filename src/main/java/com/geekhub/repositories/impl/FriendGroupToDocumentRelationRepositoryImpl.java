package com.geekhub.repositories.impl;

import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.FriendGroupToDocumentRelationRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class FriendGroupToDocumentRelationRepositoryImpl implements FriendGroupToDocumentRelationRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<FriendGroupToDocumentRelation> clazz = FriendGroupToDocumentRelation.class;

    @Override
    public List<FriendGroupToDocumentRelation> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FriendGroupToDocumentRelation getById(Long id) {
        return (FriendGroupToDocumentRelation) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public FriendGroupToDocumentRelation get(String propertyName, Object value) {
        return (FriendGroupToDocumentRelation) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(FriendGroupToDocumentRelation entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(FriendGroupToDocumentRelation entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(FriendGroupToDocumentRelation entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(FriendGroupToDocumentRelation entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        FriendGroupToDocumentRelation userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    @Override
    public List<FriendGroupToDocumentRelation> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public void deleteByDocument(UserDocument document) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FriendGroupToDocumentRelation r WHERE r.document = :document")
                .setParameter("document", document)
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
    public List<User> getAllGroupsMembersByDocument(Long documentId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.friendsGroup", "group")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("doc.id", documentId))
                .setProjection(Projections.property("group.friends"))
                .list();
    }

    @Override
    public List<FriendsGroup> getAllGroupsByDocumentId(Long documentId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("doc.id", documentId))
                .setProjection(Projections.property("friendsGroup"))
                .list();
    }

    @Override
    public List<FriendsGroup> getAllGroupsByDocumentIdAndRelation(Long documentId, FileRelationType relationType) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("doc.id", documentId))
                .add(Restrictions.eq("fileRelationType", relationType))
                .setProjection(Projections.property("friendsGroup"))
                .list();
    }

    @Override
    public List<FileRelationType> getAllRelationsByDocumentIdAndUser(Long documentId, User user) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT rel.fileRelationType FROM FriendGroupToDocumentRelation rel " +
                        "WHERE rel.document.id = :docId AND :user IN ELEMENTS(rel.friendsGroup.friends)")
                .setParameter("docId", documentId)
                .setParameter("user", user)
                .list();
    }
}
