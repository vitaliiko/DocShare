package com.geekhub.repositories;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.FileRelationType;

import java.util.List;
import java.util.Map;

public interface UserDocumentRepository extends EntityRepository<UserDocument, Long> {

    <T> List<UserDocument> getAll(String propertyName, List<T> values);

    List<UserDocument> getList(User owner, String propertyName, Object value);

    List<UserDocument> getList(Map<String, Object> propertiesMap);

    List<Object> getPropertiesList(String selectProperty, String propertyName, String value);

    UserDocument get(User owner, String propertyName, Object value);

    List<UserDocument> getLike(String parentDirectoryHash, String docName);

    UserDocument get(Map<String, Object> propertiesMap);

    UserDocument getByFullNameAndOwner(Map<String, Object> propertiesMap);

    List<UserDocument> getByUserAndRelationType(User user, FileRelationType relation);

    List<UserDocument> getByFriendGroupAndRelationType(FriendsGroup friendsGroup, FileRelationType relation);

    List<UserDocument> search(User owner, String propertyName, String value);

    void updateDocumentAttribute(DocumentAttribute attribute, List<Long> documentIds);
}
