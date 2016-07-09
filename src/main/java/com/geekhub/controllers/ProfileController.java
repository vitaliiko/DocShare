package com.geekhub.controllers;

import com.geekhub.dto.UserDto;
import com.geekhub.entities.User;
import com.geekhub.exceptions.UserProfileException;
import com.geekhub.security.UserProfileManager;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.inject.Inject;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")
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
    public ModelAndView changeProfile(UserDto userDto, HttpSession session) {
        ModelAndView model = new ModelAndView("profile");
        User user = userService.getById((Long) session.getAttribute("userId"));

        try {
            userProfileManager.updateUserProfile(userDto, user);
            model.addObject("message", "Your account updated successfully");
        } catch (UserProfileException e){
            model.addObject("errorMessage", e.getMessage());
        }
        model.addObject("user", userDto);
        return model;
    }

    @RequestMapping(value = "/profile/password", method = RequestMethod.POST)
    public ModelAndView changePassword(String currentPassword,
                                       String newPassword,
                                       String confirmNewPassword,
                                       HttpSession session) {

        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        try {
            userProfileManager.changePassword(currentPassword, newPassword, confirmNewPassword, user);
            model.addObject("message", "Your password updated successfully");
        } catch (UserProfileException e) {
            model.addObject("errorMessage", e.getMessage());
        }
        model.addObject("user", EntityToDtoConverter.convert(user));
        return model;
    }

    @RequestMapping(value = "/profile/remove", method = RequestMethod.POST)
    public ModelAndView removeAccount(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("signIn");
        if (userProfileManager.removeAccount(user)) {
            session.invalidate();
            model.addObject("message", "Your account removed successfully");
        } else {
            model.addObject("errorMessage", "Your account doesn't removed");
        }
        return model;
    }

    @RequestMapping(value = "/profile/avatar", method = RequestMethod.POST)
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

    @RequestMapping(value = "/profile/{userId}/avatar", method = RequestMethod.GET)
    public void showImage(@PathVariable Long userId, HttpServletResponse response, HttpSession session) throws IOException {
        User user = userService.getById(userId);
        byte[] avatar = user.getAvatar();
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        FileCopyUtils.copy(avatar, response.getOutputStream());
    }
}
