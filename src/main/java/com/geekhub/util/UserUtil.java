package com.geekhub.util;

import com.geekhub.entity.User;
import com.geekhub.service.UserService;
import com.geekhub.service.UserServiceImpl;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserUtil {

    @Autowired private UserService userService;
    @Autowired private SessionFactory sessionFactory;

    public void validateUser(String login, String password, String confirmPassword) throws Exception {
        if (userService.getByLogin(login) != null) {
            throw new Exception("User with such login already exist");
        }
        if (!password.equals(confirmPassword)) {
            throw new Exception("Passwords doesn't much");
        }
    }

    public void addDefaultUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("111", "111", "111", "111"));
        userList.add(new User("222", "222", "222", "222"));
        userList.add(new User("333", "333", "333", "333"));
        userList.add(new User("444", "444", "444", "444"));
        userList.add(new User("555", "555", "555", "555"));
        userList.forEach(userService::save);
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
