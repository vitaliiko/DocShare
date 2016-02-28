package com.geekhub.model;

import com.geekhub.util.DataBaseException;

import java.io.Serializable;
import java.util.List;

public interface EntityDao<T, PK extends Serializable> {

    List<T> getAllEntities(Class<T> clazz, String orderParameter) throws DataBaseException;

    T getEntityById(Class<T> clazz, PK id) throws Exception;

    T getEntity(Class<T> clazz, String propertyName, Object value) throws DataBaseException;

    PK saveEntity(T entity) throws DataBaseException;

    void updateEntity(T entity) throws DataBaseException;

    void deleteEntity(Class<T> clazz, PK entityId) throws DataBaseException;
}
