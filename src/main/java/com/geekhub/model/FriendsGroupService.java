package com.geekhub.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendsGroupService {

    @Autowired private EntityDaoImpl<FriendsGroup> friendsGroupDao;

    public void saveFriendsGroup(FriendsGroup friendsGroup) {
        friendsGroupDao.saveEntity(friendsGroup);
    }

    public List<FriendsGroup> getFrendsGroup() {
        return friendsGroupDao.getAllEntities(FriendsGroup.class, "name");
    }
}
