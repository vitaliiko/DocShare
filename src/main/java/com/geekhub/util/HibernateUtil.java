package com.geekhub.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HibernateUtil {

    @Autowired private SessionFactory sessionFactory;

    private Session session;
    private Transaction transaction;

    public Session getSession() {
        if (session == null) {
            session = sessionFactory.openSession();
        }
        return session;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public boolean isTransactionBegun() {
        return transaction != null;
    }

    public Session openSession() throws HibernateException {
        if (session != null) {
            session.close();
        }
        session = sessionFactory.openSession();
        return session;
    }

    public void closeSession() throws HibernateException {
        if (session != null) {
            session.close();
            session = null;
        } else {
            throw new DataBaseException("Session wasn't opened");
        }
    }

    public Transaction openSessionAndBeginTransaction() throws HibernateException {
        if (session == null) {
            session = sessionFactory.openSession();
        }
        transaction = session.beginTransaction();
        return transaction;
    }

    public void commitAndCloseSession() throws HibernateException {
        if (transaction != null && session != null) {
            transaction.commit();
            session.close();
        }
    }
}
