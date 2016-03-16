package com.geekhub.service;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public interface EntityService<T, PK extends Serializable> {

    List<T> getAll(String orderParameter) throws HibernateException;

    T getById(PK id) throws HibernateException;

    T get(String propertyName, Object value) throws HibernateException;

    PK save(T entity) throws HibernateException;

    void update(T entity) throws HibernateException;

    void delete(T entity) throws HibernateException;

    void delete(PK entityId) throws HibernateException;
}
