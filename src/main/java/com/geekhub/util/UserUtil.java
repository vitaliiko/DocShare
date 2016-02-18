package com.geekhub.util;

import com.geekhub.model.Message;
import com.geekhub.model.User;
import com.geekhub.model.UserService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserUtil {

    @Autowired private UserService userService;
    @Autowired private SessionFactory sessionFactory;

    private void validateUser(String login, String password, String confirmPassword) throws Exception {
        if (userService.getUserByLogin(login) != null) {
            throw new Exception("User with such login already exist");
        }
        if (!password.equals(confirmPassword)) {
            throw new Exception("Passwords doesn't much");
        }
    }

    public User createUser(String login, String password, String confirmPassword, String firstName, String lastName)
            throws Exception {
        validateUser(login, password, confirmPassword);
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    public User createUser(String login, String password, String firstName, String lastName) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    public void addDefaultUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(createUser("111", "111", "111", "111"));
        userList.add(createUser("222", "222", "111", "111"));
        userList.add(createUser("333", "333", "111", "111"));
        userList.add(createUser("444", "444", "111", "111"));
        userList.add(createUser("555", "555", "111", "111"));
        userList.forEach(user -> userService.saveUser(user));
    }

//    public void createFriends() {
//        User friend1 = createUser("user1", "111", "111", "111");
//        User friend2 = createUser("user2", "222", "222", "222");
//        User friend3 = createUser("user3", "333", "333", "333");
//        User friend4 = createUser("user4", "444", "444", "444");
//        User friend5 = createUser("user6", "666", "666", "666");
//        User friend6 = createUser("user5", "555", "555", "555");
//
//        friend1.getFriends().add(friend2);
//        friend1.getFriends().add(friend3);
//        friend1.getFriends().add(friend4);
//        friend2.getFriends().add(friend5);
//        friend2.getFriends().add(friend6);
//        friend1.getFriends().add(friend6);
//
//        Session session = sessionFactory.openSession();
//        try {
//            session.beginTransaction();
//            session.save(friend1);
//            session.save(friend2);
//            session.save(friend3);
//            session.save(friend4);
//            session.save(friend5);
//            session.save(friend6);
//            session.getTransaction().commit();
//        } finally {
//            session.close();
//        }
//    }
}
