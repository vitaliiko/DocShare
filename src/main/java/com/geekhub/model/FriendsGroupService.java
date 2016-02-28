package com.geekhub.model;

import com.geekhub.util.DataBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendsGroupService {

    @Autowired private FriendsGroupDao friendsGroupDao;

    public Integer createGroup(String name, Integer ownerId) {
        return friendsGroupDao.createGroup(name, ownerId);
    }

    public void saveFriendsGroup(FriendsGroup friendsGroup) throws DataBaseException {
        friendsGroupDao.saveEntity(friendsGroup);
    }

    public List<FriendsGroup> getFrendsGroup() throws DataBaseException {
        return friendsGroupDao.getAllEntities(FriendsGroup.class, "name");
    }

    public void addFriend(Integer groupId, Integer userId) throws DataBaseException {
        friendsGroupDao.addFriend(groupId, userId);
    }

    public void deleteFriend(Integer groupId, Integer userId) throws DataBaseException {
        friendsGroupDao.deleteFriend(groupId, userId);
    }

    public void setOwner(Integer groupId, Integer userId) throws DataBaseException {
        friendsGroupDao.setOwner(groupId, userId);
    }
}
