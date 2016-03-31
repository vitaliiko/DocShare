package com.geekhub.service;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public interface EntityService<T, PK extends Serializable> {

    List<T> getAll(String orderParameter);

    T getById(PK id);

    T get(String propertyName, Object value);

    PK save(T entity);

    void update(T entity);

    void delete(T entity);

    void delete(PK entityId);
}
