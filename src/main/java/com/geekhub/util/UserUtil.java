package com.geekhub.util;

import com.geekhub.model.User;
import com.geekhub.model.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserUtil {

    @Autowired private UserService userService;

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
        userService.saveUser(user);
        return user;
    }
}
