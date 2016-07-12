package com.geekhub.repositories.impl;

import com.geekhub.entities.Comment;
import java.util.List;

import com.geekhub.repositories.CommentRepository;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class CommentRepositoryImpl implements CommentRepository {

    @Inject
    private SessionFactory sessionFactory;

    private Class<Comment> clazz = Comment.class;

    @Override
    public List<Comment> getAll(String orderParameter) {
        return (List<Comment>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public Comment getById(Long id) {
        return (Comment) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public Comment get(String propertyName, Object value) {
        return (Comment) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<Comment> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(Comment entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(Comment entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(Comment entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(Comment entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        Comment comment = getById(entityId);
        sessionFactory.getCurrentSession().update(comment);
    }
}
