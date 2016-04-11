package com.geekhub.dao;

import com.geekhub.entity.Comment;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class CommentDao implements EntityDao<Comment, Long> {

    @Autowired
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
