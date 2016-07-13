package com.geekhub.repositories;

import com.geekhub.entities.User;

import java.util.List;
import java.util.Map;

public interface UserRepository extends EntityRepository<User, Long> {

    List<User> getByIds(List<Long> userIds);

    List<User> getByFriend(User friend);

    List<User> search(String propertyName, String value);

    List<User> search(String propertyName, String value, Map<String, String> searchingMap);
}
