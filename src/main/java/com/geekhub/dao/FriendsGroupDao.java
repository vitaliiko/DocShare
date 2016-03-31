package com.geekhub.dao;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.service.UserService;
import org.hibernate.Hibernate;
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
    public List<FriendsGroup> getAll(String orderParameter) {
        return (List<FriendsGroup>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public FriendsGroup getById(Long id) {
        FriendsGroup group = (FriendsGroup) sessionFactory.getCurrentSession()
                .get(clazz, id);
        Hibernate.initialize(group);
        return group;
    }

    @Override
    public FriendsGroup get(String propertyName, Object value) {
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
    public void delete(Long entityId) {
        FriendsGroup friendsGroup = (FriendsGroup) sessionFactory.getCurrentSession()
                .get(clazz, entityId);

        sessionFactory.getCurrentSession()
                .delete(friendsGroup);
    }

    public List<FriendsGroup> getFriendsGroups(User owner, String name) {
        return (List<FriendsGroup>) sessionFactory.getCurrentSession()
                .createQuery("from FriendsGroup fg where fg.name = :name and fg.owner = :owner")
                .setParameter("name", name)
                .setParameter("owner", owner)
                .list();
    }
}
