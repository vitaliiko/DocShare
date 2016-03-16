package com.geekhub.service;

import com.geekhub.entity.FriendsGroup;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Service
public interface FriendsGroupService extends EntityService<FriendsGroup, Long> {

    boolean addFriend(Long groupId, Long friendId) throws HibernateException;
}
