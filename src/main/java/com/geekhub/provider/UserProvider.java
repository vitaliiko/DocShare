package com.geekhub.provider;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.exception.UserValidateException;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserDirectoryService;
import com.geekhub.service.UserService;
import com.geekhub.util.UserFileUtil;
import java.io.File;
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

    public void validateUser(String login, String password, String confirmPassword) throws UserValidateException {
        if (userService.getByLogin(login) != null) {
            throw new UserValidateException("User with such login already exist");
        }
        if (!password.equals(confirmPassword)) {
            throw new UserValidateException("Passwords doesn't much");
        }
    }

    public void fillDB() {
        createRootDir();
        addDefaultUsers();
    }

    private void addDefaultUsers() {
        for (int i = 0; i < 20; i++) {
            String value = String.valueOf(i) + i + i;
            User user = new User(value, value, value, value);
            Long userId = userService.save(user);
            user.setId(userId);
            user.setRootDirectory(createUserDir(user));
            userService.update(user);
        }
        addFriends(userService.getById(1L), id -> id > 0 && id < 10);
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
        UserDirectory directory = new UserDirectory();
        directory.setParentDirectoryHash(UserFileUtil.ROOT_DIRECTORY_HASH);
        File file = new File("spring_docs");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private String createUserDir(User user) {
        UserDirectory directory = UserFileUtil.createUserDirectory(user, UserFileUtil.ROOT_DIRECTORY_HASH, user.getLogin());
        Long dirId = userDirectoryService.save(directory);
        directory = userDirectoryService.getById(dirId);
        UserFileUtil.createDirInFileSystem(directory);
        return directory.getHashName();
    }
}
