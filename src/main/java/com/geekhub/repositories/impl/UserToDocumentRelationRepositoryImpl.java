package com.geekhub.repositories.impl;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.UserToDocumentRelationRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserToDocumentRelationRepositoryImpl implements UserToDocumentRelationRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<UserToDocumentRelation> clazz = UserToDocumentRelation.class;

    @Override
    public List<UserToDocumentRelation> getAll(String orderParameter) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public UserToDocumentRelation getById(Long id) {
        return (UserToDocumentRelation) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public UserToDocumentRelation get(String propertyName, Object value) {
        return (UserToDocumentRelation) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(UserToDocumentRelation entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(UserToDocumentRelation entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(UserToDocumentRelation entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(UserToDocumentRelation entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        UserToDocumentRelation userDocument = getById(entityId);
        sessionFactory.getCurrentSession().delete(userDocument);
    }

    @Override
    public List<UserToDocumentRelation> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public void deleteAllBesidesOwnerByDocument(UserDocument document) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE UserToDocumentRelation r " +
                             "WHERE r.document = :document AND r.fileRelationType != :relation")
                .setParameter("document", document)
                .setParameter("relation", FileRelationType.OWN)
                .executeUpdate();
    }

    @Override
    public List<String> getAllDocumentHashNamesByOwner(User owner) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("user", owner))
                .add(Restrictions.eq("fileRelationType", FileRelationType.OWN))
                .setProjection(Projections.property("doc.hashName"))
                .list();
    }

    @Override
    public User getDocumentOwner(UserDocument document) {
        return (User) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("document", document))
                .add(Restrictions.eq("fileRelationType", FileRelationType.OWN))
                .setProjection(Projections.property("user"))
                .uniqueResult();
    }

    @Override
    public List<User> getAllUsersByDocumentIdBesidesOwner(Long documentId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("doc.id", documentId))
                .add(Restrictions.ne("fileRelationType", FileRelationType.OWN))
                .setProjection(Projections.property("user"))
                .list();
    }

    @Override
    public List<UserDocument> getAllAccessibleDocuments(User user) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.ne("fileRelationType", FileRelationType.OWN))
                .setProjection(Projections.property("document"))
                .list();
    }

    @Override
    public List<UserToDocumentRelation> getAllAccessibleDocumentsInRoot(User user, List<String> directoryHashes) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("user", user))
                .add(Restrictions.ne("fileRelationType", FileRelationType.OWN))
                .add(Restrictions.not(Restrictions.in("doc.parentDirectoryHash", directoryHashes)))
                .list();
    }

    @Override
    public List<User> getAllUsersByDocumentAndRelation(UserDocument document, FileRelationType relationType) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("document", document))
                .add(Restrictions.eq("fileRelationType", relationType))
                .setProjection(Projections.property("user"))
                .list();
    }

    @Override
    public UserDocument getDocumentByFullNameAndOwner(String parentDirHash, String docName, User owner) {
        return (UserDocument) sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("doc.parentDirectoryHash", parentDirHash))
                .add(Restrictions.eq("doc.name", docName))
                .add(Restrictions.eq("rel.user", owner))
                .add(Restrictions.eq("rel.fileRelationType", FileRelationType.OWN))
                .setProjection(Projections.property("document"))
                .uniqueResult();
    }

    @Override
    public List<UserDocument> getAllDocumentsByFullNamesAndOwner(String parentDirHash, List<String> docNames, User owner) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("doc.parentDirectoryHash", parentDirHash))
                .add(Restrictions.in("doc.name", docNames))
                .add(Restrictions.eq("rel.user", owner))
                .add(Restrictions.eq("rel.fileRelationType", FileRelationType.OWN))
                .setProjection(Projections.property("document"))
                .list();
    }

    @Override
    public UserToDocumentRelation getByDocumentAndUser(UserDocument document, User user) {
        return (UserToDocumentRelation) sessionFactory.getCurrentSession()
                .createQuery("FROM UserToDocumentRelation rel WHERE rel.document = :doc AND rel.user = :user")
                .setParameter("doc", document)
                .setParameter("user", user)
                .uniqueResult();
    }

    @Override
    public Long getCountByOwnerAndDocumentIds(User owner, List<Long> idList) {
        return (Long) sessionFactory.getCurrentSession()
                .createCriteria(clazz, "rel")
                .createAlias("rel.document", "doc")
                .add(Restrictions.eq("user", owner))
                .add(Restrictions.eq("fileRelationType", FileRelationType.OWN))
                .add(Restrictions.in("doc.id", idList))
                .setProjection(Projections.rowCount())
                .uniqueResult();
    }

    @Override
    public List<FileRelationType> getAllRelationsByDocumentsAndUser(List<UserDocument> documents, User user) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT rel.fileRelationType FROM UserToDocumentRelation rel " +
                             "WHERE rel.document IN :docs AND rel.user = :user")
                .setParameter("user", user)
                .setParameterList("docs", documents)
                .list();
    }
}
