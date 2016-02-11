package com.geekhub.model;

import java.util.List;

public interface EntityDao<T> {

    List<T> getAllEntities(Class<T> clazz, String orderParameter);

    T getEntityById(Class<T> clazz, long id);

    T getEntity(Class<T> clazz, String propertyName, Object value);

    void saveEntity(T entity);

    void updateEntity(T entity);

    void deleteEntity(T entity);
}
