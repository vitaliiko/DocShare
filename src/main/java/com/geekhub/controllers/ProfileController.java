package com.geekhub.controllers;

import com.geekhub.dto.UserDto;
import com.geekhub.entities.User;
import com.geekhub.exceptions.UserValidateException;
import com.geekhub.security.UserProfileManager;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/profile")
@SuppressWarnings("unchecked")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileManager userProfileManager;

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView profile(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        model.addObject("user", EntityToDtoConverter.convert(user));
        return model;
    }

    @RequestMapping(value = "/changeProfile", method = RequestMethod.POST)
    public ModelAndView changeProfile(UserDto userDto, HttpSession session) {
        ModelAndView model = new ModelAndView("profile");
        User user = userService.getById((Long) session.getAttribute("userId"));

        try {
            userProfileManager.updateUserProfile(userDto, user);
            model.addObject("message", "Your account updated successfully");
        } catch (UserValidateException e){
            model.addObject("errorMessage", e.getMessage());
        }
        model.addObject("user", userDto);
        return model;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ModelAndView changePassword(String currentPassword,
                                       String newPassword,
                                       String confirmNewPassword,
                                       HttpSession session) {

        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        try {
            userProfileManager.changePassword(currentPassword, newPassword, confirmNewPassword, user);
            model.addObject("message", "Your password updated successfully");
        } catch (UserValidateException e) {
            model.addObject("errorMessage", e.getMessage());
        }
        model.addObject("user", EntityToDtoConverter.convert(user));
        return model;
    }

    @RequestMapping("/removeAccount")
    public ModelAndView removeAccount(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        userProfileManager.removeAccount(user);
        session.invalidate();

        ModelAndView model = new ModelAndView("signIn");
        model.addObject("message", "Your account removed successfully");
        return model;
    }

    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    public ModelAndView uploadAvatar(MultipartFile avatar, HttpSession session) throws IOException {
        User user = userService.getById((Long) session.getAttribute("userId"));
        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar.getBytes());
            userService.update(user);
        }
        ModelAndView model = new ModelAndView("profile");
        model.addObject(EntityToDtoConverter.convert(user));
        model.addObject("avatar", avatar);
        return model;
    }

    @RequestMapping(value = "/avatarDisplay", method = RequestMethod.GET)
    public void showImage(Long userId, HttpServletResponse response, HttpSession session) throws IOException {
        User user = userService.getById(userId);
        byte[] avatar = user.getAvatar();
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        FileCopyUtils.copy(avatar, response.getOutputStream());
    }
}
