package com.geekhub.controller;

import com.geekhub.dto.UserDto;
import com.geekhub.entity.User;
import com.geekhub.service.UserService;
import com.geekhub.util.DtoToEntityConverter;
import com.geekhub.util.EntityToDtoConverter;
import com.geekhub.util.UserFileUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/profile")
@SuppressWarnings("unchecked")
public class ProfileController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView profile(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        model.addObject("user", EntityToDtoConverter.convert(user));
        return model;
    }

    @RequestMapping(value = "/changeProfile", method = RequestMethod.POST)
    public ModelAndView changeName(UserDto userDto, HttpSession session) {
        ModelAndView model = new ModelAndView("profile");
        User user = userService.getById((Long) session.getAttribute("userId"));

        if (user.getLogin().equals(userDto.getLogin()) || userService.getByLogin(userDto.getLogin()) == null) {
            DtoToEntityConverter.merge(userDto, user);
            userService.update(user);
            model.addObject("message", "Your account updated successfully");
        } else {
            model.addObject("errorMessage", "User with such login already exist");
        }
        model.addObject("user", userDto);
        return model;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ModelAndView changePassword(HttpSession session,
                                       String currentPassword,
                                       String newPassword,
                                       String confirmNewPassword) {

        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
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

    @RequestMapping("/removeAccount")
    public ModelAndView removeAccount(HttpSession session) {
        ModelAndView model = new ModelAndView();
        User user = userService.getById((Long) session.getAttribute("userId"));
        userService.removeFromFriends(user);
        UserFileUtil.removeUserFiles(user.getRootDirectory());
        userService.delete(user);
        session.invalidate();
        model.addObject("message", "Your account removed successfully")
                .setViewName("signIn");
        return model;
    }
}
