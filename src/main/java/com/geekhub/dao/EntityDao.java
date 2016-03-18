package com.geekhub.dao;

import org.hibernate.HibernateException;

import java.io.Serializable;
import java.util.List;

public interface EntityDao<T, PK extends Serializable> {

    List<T> getAll(String orderParameter) throws HibernateException;

    T getById(PK id) throws HibernateException;

    T get(String propertyName, Object value) throws HibernateException;

    PK save(T entity) throws HibernateException;

    void update(T entity) throws HibernateException;

    void saveOrUpdate(T entity) throws HibernateException;

    void delete(T entity) throws HibernateException;

    void delete(PK entityId) throws HibernateException;
}
