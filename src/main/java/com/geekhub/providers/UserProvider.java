package com.geekhub.providers;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.exceptions.UserValidateException;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserService;
import java.io.File;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.LongPredicate;

@Service
public class UserProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendsGroupService friendsGroupService;

    @Autowired
    private UserDirectoryService userDirectoryService;

    public void fillDB() {
        if (userService.getAll("id").size() == 0) {
            createRootDir();
            addDefaultUsers();
        }
    }

    private void addDefaultUsers() {
        for (int i = 0; i < 20; i++) {
            String value = String.valueOf(i) + i + i;
            User user = new User(value, value, DigestUtils.sha1Hex(value), value);
            Long userId = userService.save(user);
            user.setId(userId);
            userService.update(user);
        }
        addFriends(userService.getById(1L), id -> id > 1 && id < 10);
        addGroup(1L, "Parents", id -> id > 2 && id < 5);
        addGroup(1L, "Fuckers", id -> id > 3 && id < 8);
        addFriends(userService.getById(2L), id -> id > 10 && id < 18);
        addGroup(2L, "Fuckers", id -> id > 13 && id < 18);
    }

    private void addFriends(User user, LongPredicate predicate) {
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

    private void createRootDir() {
        File file = new File("C:\\spring_docs");
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
