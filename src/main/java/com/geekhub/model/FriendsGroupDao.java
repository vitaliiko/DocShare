package com.geekhub.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FriendsGroupDao extends EntityDaoImpl<FriendsGroup> {

    @Autowired private SessionFactory sessionFactory;

    public void addFriend(Integer groupId, Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FriendsGroup group = (FriendsGroup) session.get(FriendsGroup.class, groupId);
            User user = (User) session.get(User.class, userId);
            group.getFriendsSet().add(user);
            session.update(group);
            session.update(user);
            session.getTransaction().commit();
            System.out.println("FRIENDS: ");
            group.getFriendsSet().forEach(user1 -> System.out.println(user1.toString()));
        } catch (Exception e) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void setOwner(Integer groupId, Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FriendsGroup group = (FriendsGroup) session.get(FriendsGroup.class, groupId);
            User user = (User) session.get(User.class, userId);
            group.setOwner(user);
            session.update(group);
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
