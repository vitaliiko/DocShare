package com.geekhub.service;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface FriendsGroupService extends EntityService<FriendsGroup, Long> {

    boolean addFriend(Long groupId, Long friendId) throws HibernateException;

    FriendsGroup getByName(String groupName) throws HibernateException;

    Set<User> getFriendsSet(FriendsGroup group) throws HibernateException;
}
