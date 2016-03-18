package com.geekhub.service;

import com.geekhub.dao.FriendsGroupDao;
import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FriendsGroupServiceImpl implements FriendsGroupService {

    @Autowired
    private FriendsGroupDao friendsGroupDao;

    @Autowired
    private UserService userService;

    @Override
    public List<FriendsGroup> getAll(String orderParameter) throws HibernateException {
        return friendsGroupDao.getAll(orderParameter);
    }

    @Override
    public FriendsGroup getById(Long id) throws HibernateException {
        return friendsGroupDao.getById(id);
    }

    @Override
    public FriendsGroup get(String propertyName, Object value) throws HibernateException {
        return friendsGroupDao.get(propertyName, value);
    }

    @Override
    public Long save(FriendsGroup entity) throws HibernateException {
        return friendsGroupDao.save(entity);
    }

    @Override
    public void update(FriendsGroup entity) throws HibernateException {
        friendsGroupDao.update(entity);
    }

    @Override
    public void delete(FriendsGroup entity) throws HibernateException {
        friendsGroupDao.delete(entity);
    }

    @Override
    public void delete(Long entityId) throws HibernateException {
        friendsGroupDao.delete(entityId);
    }

    @Override
    public boolean addFriend(Long groupId, Long friendId) throws HibernateException {
        FriendsGroup group = friendsGroupDao.getById(friendId);
        User user = userService.getById(friendId);
        if (group.getFriendsSet().add(user)) {
            friendsGroupDao.update(group);
            return true;
        }
        return false;
    }

    @Override
    public FriendsGroup getByName(String groupName) throws HibernateException {
        return friendsGroupDao.get("name", groupName);
    }
}
