package com.geekhub.dao;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@SuppressWarnings("unchecked")
public class UserDao implements EntityDao<User, Long> {

    @Autowired
    private SessionFactory sessionFactory;
    private Class<User> clazz = User.class;

    @Override
    public List<User> getAll(String orderParameter) {
        return (List<User>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public User getById(Long id) {
        return (User) sessionFactory.getCurrentSession()
                .get(clazz, id);
    }

    @Override
    public User get(String propertyName, Object value) {
        List<User> list = (List<User>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Long save(User entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(User entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(User entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(User entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void delete(Long entityId) {
        User user = getById(entityId);
        sessionFactory.getCurrentSession().delete(user);
    }

    public FriendsGroup getFriendsGroup(Long userId, String groupName) {
        User owner = getById(userId);
        return (FriendsGroup) sessionFactory.getCurrentSession()
                .createQuery("from FriendsGroup fg where fg.name = :name and fg.owner = :owner")
                .setParameter("name", groupName)
                .setParameter("owner", owner)
                .uniqueResult();
    }

    public List<FriendsGroup> getFriendsGroups(Long userId) {
        User owner = getById(userId);
        return owner.getFriendsGroups().stream().collect(Collectors.toList());
    }

    public Set<User> getFriends(Long userId) {
        User user = getById(userId);
        Hibernate.initialize(user.getFriends());
        return user.getFriends();
    }

    public void addFriendsGroup(Long userId, FriendsGroup group) {
        User user = getById(userId);
        if (user.getFriendsGroups().stream().noneMatch(fg -> fg.getName().equals(group.getName()))) {
            user.getFriendsGroups().add(group);
        } else {
            throw new HibernateException("Friends Group with such name already exist");
        }
        sessionFactory.getCurrentSession().update(user);
    }

    public List<FriendsGroup> getGroupsByOwnerAndFriend(Long ownerId, User friend) {
        User owner = getById(ownerId);
        return sessionFactory.getCurrentSession()
                .createQuery("from FriendsGroup fg " +
                        "where fg.owner = :owner " +
                        "and :friend in elements(fg.friends)")
                .setParameter("owner", owner)
                .setParameter("friend", friend)
                .list();
    }

    public void removeFriendFromGroups() {

    }

    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().add(friend);
        sessionFactory.getCurrentSession().update(user);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().remove(friend);
        sessionFactory.getCurrentSession().update(user);
    }
 }
