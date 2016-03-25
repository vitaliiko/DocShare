package com.geekhub.util;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.exception.UserValidateException;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;

@Service
public class UserUtil {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendsGroupUtil friendsGroupUtil;

    @Autowired
    private FriendsGroupService friendsGroupService;

    public void validateUser(String login, String password, String confirmPassword) throws UserValidateException {
        if (userService.getByLogin(login) != null) {
            throw new UserValidateException("User with such login already exist");
        }
        if (!password.equals(confirmPassword)) {
            throw new UserValidateException("Passwords doesn't much");
        }
    }

    public Long createUser(String firstName, String lastName, String login, String password) throws HibernateException {
        User user = new User(firstName, lastName, password, login);
        return userService.save(user);
    }

    public void addDefaultUsers() {
        for (int i = 0; i < 20; i++) {
            String value = String.valueOf(i) + i + i;
            User user = new User(value, value, value, value);
            userService.save(user);
        }
        addFriends(userService.getById(1L), id -> id > 0 && id < 10);
        addGroup(1L, "Parents", id -> id > 2 && id < 5);
        addGroup(1L, "Fuckers", id -> id > 3 && id < 8);
        addFriends(userService.getById(2L), id -> id > 10 && id < 18);
        addGroup(2L, "Fuckers", id -> id > 13 && id < 18);
    }

    public void addFriends(User user, LongPredicate predicate) {
        List<User> userList = userService.getAll("id");
        userList.stream()
                .filter(u -> predicate.test(u.getId()))
                .forEach(u -> {
                    userService.addFriend(user.getId(), u.getId());
                    userService.addFriend(u.getId(), user.getId());
                });
        userService.update(user);
    }

    private void addGroup(Long userId, String name, LongPredicate predicate) {
        FriendsGroup group = new FriendsGroup(name);
        List<User> users = userService.getAll("id");
        users.stream()
                .filter(u -> predicate.test(u.getId()))
                .forEach(group.getFriends()::add);
        userService.addFriendsGroup(userId, group);
    }

    public Long addFriendsGroup(Long userId, String name, Long[] friendsIds) {
        Set<User> friendsSet = new HashSet<>();
        Arrays.stream(friendsIds).forEach(id -> friendsSet.add(userService.getById(id)));
        FriendsGroup group = new FriendsGroup(name, friendsSet);
        group.setOwner(userService.getById(userId));
        return friendsGroupService.save(group);
    }

    public Map<User, List<FriendsGroup>> getFriendsWithGroups(Long userId) {
        Set<User> friends = userService.getFriends(userId);
        Map<User, List<FriendsGroup>> friendsMap = new HashMap<>();
        friends.forEach(f -> friendsMap.put(f, userService.getGroupsByOwnerAndFriend(userId, f)));
        return friendsMap;
    }

    public boolean areFriends(Long userId, User friend) {
        Set<User> friends = userService.getFriends(userId);
        return friends.contains(friend);
    }

    public List<User> getAllWithoutCurrentUser(Long userId) {
        return userService.getAll("id").stream()
                .filter(u -> !u.getId().equals(userId))
                .collect(Collectors.toList());
    }
}
