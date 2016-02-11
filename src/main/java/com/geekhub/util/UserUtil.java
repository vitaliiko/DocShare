package com.geekhub.util;

import com.geekhub.model.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class UserUtil {

    @Autowired private UserService userService;

    public boolean validateUser(String login, String password, String confirmPassword, ModelMap model) {
        if (userService.getUserByLogin(login) != null) {
            model.addAttribute("errorMessage", "User with such login already exist");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords doesn't much");
            return false;
        }
        return true;
    }
}
