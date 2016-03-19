package com.geekhub.dao;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.service.UserService;
import org.hibernate.HibernateException;
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

    @Autowired
    private UserService userService;

    private Class<FriendsGroup> clazz = FriendsGroup.class;

    @Override
    public List<FriendsGroup> getAll(String orderParameter) throws HibernateException {
        return (List<FriendsGroup>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FriendsGroup getById(Long id) throws HibernateException {
        return (FriendsGroup) sessionFactory.getCurrentSession()
                .get(clazz, id);
    }

    @Override
    public FriendsGroup get(String propertyName, Object value) throws HibernateException {
        List<FriendsGroup> list = (List<FriendsGroup>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Long save(FriendsGroup entity) throws HibernateException {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(FriendsGroup entity) throws HibernateException {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(FriendsGroup entity) throws HibernateException {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(FriendsGroup entity) throws HibernateException {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void delete(Long entityId) throws HibernateException {
        FriendsGroup friendsGroup = (FriendsGroup) sessionFactory.getCurrentSession()
                .get(clazz, entityId);

        sessionFactory.getCurrentSession()
                .delete(friendsGroup);
    }

    public List<FriendsGroup> getByOwnerAndFriend(Long ownerId, User friend) throws HibernateException {
        User owner = userService.getById(ownerId);
        return sessionFactory.getCurrentSession()
                .createQuery("from FriendsGroup fg " +
                        "where fg.owner = :owner " +
                        "and :friend in elements(fg.friendsSet) " +
                        "and fg.name != 'Friends'")
                .setParameter("owner", owner)
                .setParameter("friend", friend)
                .list();
    }
}
