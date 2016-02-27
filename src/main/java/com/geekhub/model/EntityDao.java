package com.geekhub.model;

import com.geekhub.util.DataBaseException;

import java.util.List;

public interface EntityDao<T> {

    List<T> getAllEntities(Class<T> clazz, String orderParameter) throws DataBaseException;

    T getEntityById(Class<T> clazz, int id) throws Exception;

    T getEntity(Class<T> clazz, String propertyName, Object value) throws DataBaseException;

    void saveEntity(T entity) throws DataBaseException;

    void updateEntity(T entity) throws DataBaseException;

    void deleteEntity(T entity) throws DataBaseException;

    void deleteEntity(Class<T> clazz, Integer entityId) throws DataBaseException;
}
