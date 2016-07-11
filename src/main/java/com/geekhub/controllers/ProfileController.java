package com.geekhub.controllers;

import com.geekhub.dto.UserDto;
import com.geekhub.entities.User;
import com.geekhub.exceptions.ValidateUserInformationException;
import com.geekhub.security.UserProfileManager;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Inject
    private UserService userService;

    @Inject
    private UserProfileManager userProfileManager;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        model.addObject("user", EntityToDtoConverter.convert(user));
        return model;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView changeProfile(UserDto userDto, HttpSession session) throws ValidateUserInformationException {
        ModelAndView model = new ModelAndView("profile");
        User user = userService.getById((Long) session.getAttribute("userId"));
        userProfileManager.updateUserProfile(userDto, user);

        model.addObject("message", "Your account updated successfully");
        model.addObject("user", userDto);
        return model;
    }

    @RequestMapping(value = "/profile/password", method = RequestMethod.POST)
    public ModelAndView changePassword(@RequestParam String currentPassword,
                                       @RequestParam String newPassword,
                                       HttpSession session) throws ValidateUserInformationException {

        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        userProfileManager.changePassword(currentPassword, newPassword, user);

        model.addObject("message", "Your password updated successfully");
        model.addObject("user", EntityToDtoConverter.convert(user));
        return model;
    }

    @RequestMapping(value = "/profile/remove", method = RequestMethod.POST)
    public ModelAndView removeAccount(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("signIn");
        if (user != null) {
            userService.delete(user);
            session.invalidate();
            model.addObject("message", "Your account removed successfully");
        }
        return model;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidateUserInformationException.class)
    public ModelAndView handlingUserProfileException(ValidateUserInformationException e) {
        ModelAndView model = new ModelAndView("profile");
        model.addObject("errorMessage", e.getMessage());
        if (e.getUserDto() != null) {
            model.addObject("user", e.getUserDto());
        }
        return model;
    }
}
