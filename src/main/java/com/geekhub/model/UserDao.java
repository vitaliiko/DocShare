package com.geekhub.model;

import com.geekhub.util.UserUtil;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao extends EntityDaoImpl<User> {

    @Autowired private SessionFactory sessionFactory;

    public void addMessage(User user, Message message) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            user.getMessageSet().add(message);
            session.update(user);
            session.getTransaction().commit();
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
            if (userList.size() > 0) {
                user.getFriends().add(userList.get(0));
                session.update(user);
            } else {
                System.out.println("empty user list. not found user with login " + friendLogin);
            }

            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
