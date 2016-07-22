package com.geekhub.repositories;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;

import java.util.List;
import java.util.Map;

public interface UserDirectoryRepository extends EntityRepository<UserDirectory, Long> {

    <T> List<UserDirectory> getAll(String propertyName, List<T> values);

    List<UserDirectory> getList(User owner, String propertyName, Object value);

    List<UserDirectory> getList(Map<String, Object> propertiesMap);

    List<Object> getPropertiesList(String selectProperty, String propertyName, String value);

    List<UserDirectory> getLike(String parentDirectoryHash, String dirName);

    UserDirectory get(User owner, String propertyName, Object value);

    UserDirectory getByFullNameAndOwner(Map<String, Object> propertiesMap);

    List<UserDirectory> search(User owner, String propertyName, String value);

    void updateDocumentAttribute(DocumentAttribute attribute, List<Long> directoryIds);
}
