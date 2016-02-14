package com.geekhub.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

}
