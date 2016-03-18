package com.geekhub.util;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.exception.UserValidateException;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserService;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        for (int i = 0; i < 10; i++) {
            String value = String.valueOf(i) + i + i;
            User user = new User(value, value, value, value);
            user.getOwnerGroupSet().add(friendsGroupUtil.createDefaultGroup());
            userService.save(user);
            addFriends(1L);
        }
    }

    public void addFriends(Long userId) {
        List<User> userList = userService.getAll("id");
        FriendsGroup group = userService.getFriendsGroup(userId, "Friends");
        Hibernate.initialize(group.getFriendsSet());
        userList.stream()
                .filter(u -> u.getId().equals(userId))
                .forEach(group.getFriendsSet()::add);
        friendsGroupService.save(group);
    }

    public void printFriends(Long id) {
        User user = userService.getById(id);
        System.out.println("userId: " + user.getId());
        System.out.println("Owner Groups:");
        user.getOwnerGroupSet().forEach(System.out::println);
        System.out.println("Foreign Groups:");
        user.getForeignGroupSet().forEach(System.out::println);
    }
}
