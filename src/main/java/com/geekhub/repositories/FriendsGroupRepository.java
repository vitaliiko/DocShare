package com.geekhub.repositories;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;

import java.util.List;

public interface FriendsGroupRepository extends EntityRepository<FriendsGroup, Long> {

    List<FriendsGroup> getByIds(List<Long> groupIds);

    FriendsGroup get(User owner, String propertyName, Object value);

    List<FriendsGroup> getFriendsGroups(User owner, String propertyName, Object value);

    List<FriendsGroup> getByOwnerAndFriend(User owner, User friend);

    List<FriendsGroup> getByFriend(User friend);
}
