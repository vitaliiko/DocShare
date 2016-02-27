package com.geekhub.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends EntityDaoImpl<User> {

    @Autowired private SessionFactory sessionFactory;

    public void addMessage(Integer id, Message message) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            User user = (User) session.get(User.class, id);
            user.getMessageSet().add(message);
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void addMessage(User user, Message message) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            user.getMessageSet().add(message);
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteMessage(Integer userId, Integer messageId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            User user = (User) session.get(User.class, userId);
            Message message = (Message) session.get(Message.class, messageId);
            user.getMessageSet().remove(message);
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void addFriend(User user, String friendLogin) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            List<User> userList = (List<User>) session.createCriteria(User.class)
                    .add(Restrictions.eq("login", friendLogin))
                    .list();
//            if (userList.size() > 0 && !userList.get(0).equals(user)) {
//                session.update(user);
//            } else {
//                System.out.println("empty user list. not found user with login " + friendLogin);
//            }

            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void deleteUser(Integer userId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            User user = (User) session.get(User.class, userId);
            user.getMessageSet().clear();
            session.delete(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
