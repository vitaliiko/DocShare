package com.geekhub.dao;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class FriendsGroupDao implements EntityDao<FriendsGroup, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<FriendsGroup> clazz = FriendsGroup.class;

    @Override
    public List<FriendsGroup> getAll(String orderParameter) {
        return (List<FriendsGroup>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FriendsGroup getById(Long id) {
        return (FriendsGroup) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public FriendsGroup get(String propertyName, Object value) {
        return (FriendsGroup) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public Long save(FriendsGroup entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(FriendsGroup entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(FriendsGroup entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(FriendsGroup entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        FriendsGroup friendsGroup = getById(entityId);
        sessionFactory.getCurrentSession().delete(friendsGroup);
    }

    public List<FriendsGroup> getFriendsGroups(User owner, String propertyName, Object value) {
        return (List<FriendsGroup>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("owner", owner))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    public List<FriendsGroup> getByOwnerAndFriend(User owner, User friend) {
        return sessionFactory.getCurrentSession()
                .createQuery("from FriendsGroup fg " +
                        "where fg.owner = :owner " +
                        "and :friend in elements(fg.friends)")
                .setParameter("owner", owner)
                .setParameter("friend", friend)
                .list();
    }

    public List<FriendsGroup> getByFriend(User friend) {
        return sessionFactory.getCurrentSession()
                .createQuery("from FriendsGroup fg where :friend in elements(fg.friends)")
                .setParameter("friend", friend)
                .list();
    }
}
