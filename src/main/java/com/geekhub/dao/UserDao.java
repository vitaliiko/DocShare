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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@SuppressWarnings("unchecked")
//@NamedQueries({
//        @NamedQuery(name = "getFriends", query = "from friendsgroup fg where fg.name = :name and fg.userId = :userId")
//})

public class UserDao implements EntityDao<User, Long> {

    @Autowired
    private SessionFactory sessionFactory;
    private Class<User> clazz = User.class;

    @Override
    public List<User> getAll(String orderParameter) throws HibernateException {
        return (List<User>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public User getById(Long id) throws HibernateException {
        return (User) sessionFactory.getCurrentSession()
                .get(clazz, id);
    }

    @Override
    public User get(String propertyName, Object value) throws HibernateException {
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
    public Long save(User entity) throws HibernateException {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(User entity) throws HibernateException {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(User entity) throws HibernateException {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(User entity) throws HibernateException {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void delete(Long entityId) throws HibernateException {
        User user = getById(entityId);
        sessionFactory.getCurrentSession().delete(user);
    }

    public FriendsGroup getFriendsGroup(Long userId, String groupName) throws HibernateException {
        User owner = getById(userId);
        FriendsGroup group = (FriendsGroup) sessionFactory.getCurrentSession()
                .createQuery("from FriendsGroup fg where fg.name = :name and fg.owner = :owner")
                .setParameter("name", groupName)
                .setParameter("owner", owner)
                .uniqueResult();
        Hibernate.initialize(group.getFriendsSet());
        return group;
    }

    public List<FriendsGroup> getFriendsGroups(Long userId) throws HibernateException {
        User owner = getById(userId);
        Hibernate.initialize(owner.getOwnerGroupSet());
        return owner.getOwnerGroupSet().stream()
                .filter(fg -> !fg.getName().equals("Friends"))
                .collect(Collectors.toList());
    }

    public Set<User> getFriends(Long userId) throws HibernateException {
        FriendsGroup group = getFriendsGroup(userId, "Friends");
        Hibernate.initialize(group.getFriendsSet());
        return group.getFriendsSet();
    }

    public void addFriendsGroup(Long userId, String groupName) throws HibernateException {
        User user = getById(userId);
        Hibernate.initialize(user.getOwnerGroupSet());
        user.getOwnerGroupSet().add(new FriendsGroup(groupName));
        sessionFactory.getCurrentSession().update(user);
    }

    public Set<FriendsGroup> getForeignGroups(Long userId) throws HibernateException {
        User user = getById(userId);
        Hibernate.initialize(user.getForeignGroupSet());
        return user.getForeignGroupSet();
    }

    public List<FriendsGroup> getByOwnerAndFriend(Long ownerId, User friend) throws HibernateException {
        User owner = getById(ownerId);
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
