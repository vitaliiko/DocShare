package com.geekhub.controller;

import com.geekhub.entity.User;
import com.geekhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
@SuppressWarnings("unchecked")
public class ProfileController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView profile(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        model.addObject("login", user.getLogin())
                .addObject("firstName", user.getFirstName())
                .addObject("lastName", user.getLastName());
        return model;
    }

    @RequestMapping(value = "/changeName", method = RequestMethod.POST)
    public ModelAndView changeName(String login, String firstName, String lastName, HttpSession session) {
        ModelAndView model = new ModelAndView("profile");
        User user = userService.getById((Long) session.getAttribute("userId"));
        if (user.getLogin().equals(login) || userService.getByLogin(login) == null) {
            user.setLogin(login);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userService.update(user);
            model.addObject("message", "Your account updated successfully");
        } else {
            model.addObject("errorMessage", "User with such login already exist");
        }
        return model;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ModelAndView changePassword(HttpSession session,
                                       String currentPassword,
                                       String newPassword,
                                       String confirmNewPassword) {

        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("pages/profile");
        if (!user.getPassword().equals(currentPassword)) {
            model.addObject("errorMessage", "Wrong password");
        } else if (!newPassword.equals(confirmNewPassword)) {
            model.addObject("errorMessage", "Passwords doesn't match");
        } else {
            user.setPassword(newPassword);
            userService.update(user);
            model.addObject("message", "Your password was changed successfully");
        }
        return model;
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.GET)
    public ModelAndView removeAccount(HttpSession session) {
        ModelAndView model = new ModelAndView();
        userService.delete((Long) session.getAttribute("userId"));
        session.invalidate();
        model.addObject("message", "Your account removed successfully")
                .setViewName("pages/signIn");
        return model;
    }
}
