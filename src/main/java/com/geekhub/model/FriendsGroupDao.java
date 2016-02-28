package com.geekhub.model;

import com.geekhub.util.DataBaseException;
import com.geekhub.util.HibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FriendsGroupDao extends EntityDaoImpl<FriendsGroup, Integer> {

    @Autowired private HibernateUtil hibernateUtil;
    @Autowired private UserService userService;

    public Integer createGroup(String name, Integer ownerId) {
        hibernateUtil.openSessionAndBeginTransaction();
        User user = userService.getUserById(ownerId);
        FriendsGroup group = new FriendsGroup();
        group.setOwner(user);
        group.setName(name);
        Integer id = saveEntity(group);
        hibernateUtil.commitAndCloseSession();
        return id;
    }

    public void addFriend(Integer groupId, Integer userId) throws DataBaseException {
        hibernateUtil.openSessionAndBeginTransaction();
        FriendsGroup group = getEntityById(FriendsGroup.class, groupId);
        User user = userService.getUserById(userId);
        group.getFriendsSet().add(user);
        updateEntity(group);
        hibernateUtil.commitAndCloseSession();
    }

    public void deleteFriend(Integer groupId, Integer userId) throws DataBaseException {
        hibernateUtil.openSessionAndBeginTransaction();
        FriendsGroup group = getEntityById(FriendsGroup.class, groupId);
        User user = userService.getUserById(userId);
        group.getFriendsSet().remove(user);
        updateEntity(group);
        hibernateUtil.commitAndCloseSession();
    }

    public void setOwner(Integer groupId, Integer userId) throws DataBaseException {
        hibernateUtil.openSessionAndBeginTransaction();
        FriendsGroup group = getEntityById(FriendsGroup.class, groupId);
        User user = userService.getUserById(userId);
        group.setOwner(user);
        updateEntity(group);
        hibernateUtil.commitAndCloseSession();
    }
}
