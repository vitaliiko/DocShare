package com.geekhub.util;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.exception.UserValidateException;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        user.getOwnerGroupSet().add(friendsGroupUtil.createDefaultGroup());
        return userService.save(user);
    }

    public void addDefaultUsers() {
        for (int i = 0; i < 20; i++) {
            String value = String.valueOf(i) + i + i;
            User user = new User(value, value, value, value);
            user.getOwnerGroupSet().add(friendsGroupUtil.createDefaultGroup());
            userService.save(user);
        }
        addFriends(1L, id -> id > 0 && id < 10);
        addGroup(1L, "Parents");
        addFriends(2L, id -> id > 10 && id < 18);
    }

    public void addFriends(Long userId, LongPredicate predicate) {
        List<User> userList = userService.getAll("id");
        FriendsGroup group = userService.getFriendsGroup(userId, "Friends");
        userList.stream()
                .filter(u -> predicate.test(u.getId()))
                .forEach(group.getFriendsSet()::add);
        friendsGroupService.update(group);
    }

    public void addGroup(Long userId, String name) {
        userService.addFriendsGroup(userId, name);
        FriendsGroup group = userService.getFriendsGroup(userId, name);
        List<User> users = userService.getAll("id");
        users.stream()
                .filter(u -> u.getId() > 5 && u.getId() < 9)
                .forEach(group.getFriendsSet()::add);
        friendsGroupService.update(group);
    }

    public Map<User, List<FriendsGroup>> getFriendsWithGroups(Long userId) {
        Set<User> friends = userService.getFriends(userId);
        Map<User, List<FriendsGroup>> friendsMap = new HashMap<>();
        friends.forEach(f -> friendsMap.put(f, friendsGroupService.getByOwnerAndFriend(userId, f)));
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
