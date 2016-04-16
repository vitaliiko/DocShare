package com.geekhub.services;

import com.geekhub.dao.UserDao;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private FriendsGroupService friendsGroupService;

    @Override
    public List<User> getAll(String orderParameter) {
        return userDao.getAll(orderParameter);
    }

    @Override
    public User getById(Long id) {
        return userDao.getById(id);
    }

    @Override
    public User get(String propertyName, Object value) {
        return userDao.get(propertyName, value);
    }

    @Override
    public Long save(User entity) {
        if (userDao.get("login", entity.getLogin()) == null) {
            return userDao.save(entity);
        }
        return null;
    }

    @Override
    public void update(User entity) {
        userDao.update(entity);
    }

    @Override
    public void delete(User entity) {
        userDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        userDao.deleteById(entityId);
    }

    @Override
    public User getByLogin(String login) {
        return userDao.get("login", login);
    }

    @Override
    public Set<User> getFriends(Long userId) {
        User user = userDao.getById(userId);
        return user.getFriends();
    }

    @Override
    public FriendsGroup getFriendsGroupByName(Long ownerId, String groupName) {
        User owner = userDao.getById(ownerId);
        return friendsGroupService.getFriendsGroups(owner, "name", groupName).get(0);
    }

    @Override
    public List<FriendsGroup> getAllFriendsGroups(Long ownerId) {
        User owner = userDao.getById(ownerId);
        return owner.getFriendsGroups().stream().collect(Collectors.toList());
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        User owner = userDao.getById(userId);
        return owner.getFriends().stream().collect(Collectors.toList());
    }

    @Override
    public List<FriendsGroup> getGroupsByOwnerAndFriend(Long ownerId, User friend) {
        User owner = userDao.getById(ownerId);
        return friendsGroupService.getByOwnerAndFriend(owner, friend);
    }

    @Override
    public void addFriendsGroup(Long ownerId, FriendsGroup group) {
        User user = userDao.getById(ownerId);
        if (user.getFriendsGroups().stream().noneMatch(fg -> fg.getName().equals(group.getName()))) {
            user.getFriendsGroups().add(group);
        } else {
            throw new HibernateException("Friends Group with such name already exist");
        }
        userDao.update(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userDao.getById(userId);
        User friend = userDao.getById(friendId);
        user.getFriends().add(friend);
        userDao.update(user);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userDao.getById(userId);
        User friend = userDao.getById(friendId);

        user.getFriends().remove(friend);
        userDao.update(user);

        friend.getFriends().remove(user);
        userDao.update(user);
    }

    @Override
    public boolean areFriends(Long userId, User friend) {
        Set<User> friends = userDao.getById(userId).getFriends();
        return friends.contains(friend);
    }

    @Override
    public Map<User, List<FriendsGroup>> getFriendsGroupsMap(Long ownerId) {
        Set<User> friends = userDao.getById(ownerId).getFriends();
        Map<User, List<FriendsGroup>> friendsGroupsMap = new HashMap<>();
        friends.forEach(friend -> friendsGroupsMap.put(friend, getGroupsByOwnerAndFriend(ownerId, friend)));
        return friendsGroupsMap;
    }

    @Override
    public void removeFromFriends(User friend) {
        List<User> users = userDao.getByFriend(friend);
        users.forEach(u -> u.getFriends().remove(friend));
        update(users);

        List<FriendsGroup> groups = friendsGroupService.getByFriend(friend);
        groups.forEach(g -> g.getFriends().remove(friend));
        friendsGroupService.update(groups);
    }

    @Override
    public void update(List<User> users) {
        users.forEach(this::update);
    }

    @Override
    public Set<User> getSetByIds(Long[] usersIds) {
        Set<User> users = new HashSet<>();
        Arrays.stream(usersIds).forEach(id -> users.add(getById(id)));
        return users;
    }

    @Override
    public void clearEvents(Long userId) {
        User user = getById(userId);
        user.getEvents().clear();
        update(user);
    }

    @Override
    public Set<User> searchByName(String name) {
        String[] names = name.split(" ");
        Set<User> users = new TreeSet<>();
        Arrays.stream(names).forEach(n -> users.addAll(userDao.search("firstName", n)));
        Arrays.stream(names).forEach(n -> users.addAll(userDao.search("lastName", n)));
        return users;
    }
}
