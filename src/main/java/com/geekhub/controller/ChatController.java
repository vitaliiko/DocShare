package com.geekhub.controller;

import com.geekhub.model.*;
import com.geekhub.util.FriendsGroupUtil;
import com.geekhub.util.MessageUtil;
import com.geekhub.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;

@Controller
@SessionAttributes("userId")
public class ChatController {

    @Autowired private UserService userService;
    @Autowired private UserUtil userUtil;
    @Autowired private MessageService messageService;
    @Autowired private MessageUtil messageUtil;
    @Autowired private FriendsGroupService friendsGroupService;
    @Autowired private FriendsGroupUtil friendsGroupUtil;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView model = new ModelAndView("index");
        model.addObject("messages", messageService.getMessages());
        return model;
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.POST)
    public String index(@ModelAttribute Integer userId, @RequestParam String messageText) {
        Message message = new Message();
        message.setText(messageUtil.detectLink(messageText));
        message.setDate(Calendar.getInstance().getTime());
        userService.addMessage(userId, message);
        return "redirect:/index";
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.GET)
    public String login() {
        return "signIn";
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public ModelAndView login(String login, String password) {
        User user = userService.getUserByLogin(login);
        ModelAndView model = new ModelAndView();
        if (user != null && user.getPassword().equals(password)) {
            model.addObject("userId", user.getId())
                    .setViewName("redirect:/index");
        } else {
            model.addObject("errorMessage", "Wrong login or password")
                    .setViewName("signIn");
        }
        return model;
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.GET)
    public String signUp() {
        return "signUp";
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public ModelAndView signUp(String firstName,
                         String lastName,
                         String login,
                         String password,
                         String confirmPassword) {

        ModelAndView model = new ModelAndView();
        try {
            userUtil.validateUser(login, password, confirmPassword);
            User user = new User(login, password, firstName, lastName);
            userService.saveUser(user);
            model.setViewName("signIn");
        } catch (Exception e) {
            model.addObject("login", login)
                    .addObject("firstName", firstName)
                    .addObject("lastName", lastName)
                    .addObject("errorMessage", e.getMessage())
                    .setViewName("signUp");
        }
        return model;
    }

    @RequestMapping("/signOut")
    public String signOut(HttpSession session, HttpServletRequest req) {
        session.invalidate();
        req.removeAttribute("userId");
        return "signIn";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(Integer userId) {
        User user = userService.getUserById(userId);
        ModelAndView model = new ModelAndView("profile");
        model.addObject("login", user.getLogin())
                .addObject("firstName", user.getFirstName())
                .addObject("lastName", user.getLastName());
        return model;
    }

    @RequestMapping(value = "/changeName", method = RequestMethod.POST)
    public ModelAndView changeName(Integer userId, String login, String firstName, String lastName) {
        ModelAndView model = new ModelAndView("profile");
        User user = userService.getUserById(userId);
        if (user.getLogin().equals(login) || userService.getUserByLogin(login) == null) {
            user.setLogin(login);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userService.updateUser(user);
            model.addObject("message", "Your account updated successfully");
        } else {
            model.addObject("errorMessage", "User with such login already exist");
        }
        return model;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ModelAndView changePassword(@ModelAttribute User user,
                                 String currentPassword,
                                 String newPassword,
                                 String confirmNewPassword) {

        ModelAndView model = new ModelAndView("profile");
        if (!user.getPassword().equals(currentPassword)) {
            model.addObject("errorMessage", "Wrong password");
        } else if (!newPassword.equals(confirmNewPassword)) {
            model.addObject("errorMessage", "Passwords doesn't match");
        } else {
            user.setPassword(newPassword);
            userService.updateUser(user);
            model.addObject("message", "Your password was changed successfully");
        }
        return model;
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.GET)
    public String removeAccount() {
        return "confirmRemovingAccount";
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.POST)
    public ModelAndView removeAccount(Integer userId, String yes, HttpSession session, HttpServletRequest req) {
        ModelAndView model = new ModelAndView();
        if (yes != null) {
            userService.deleteUser(userId);
            session.invalidate();
            req.removeAttribute("userId");
            model.addObject("message", "Your account removed successfully")
                    .setViewName("signIn");
            return model;
        }
        model.setViewName("profile");
        return model;
    }

    @RequestMapping("/default")
    public String createDefaultUsers() {
        userUtil.addDefaultUsers();
        return "signIn";
    }

    @RequestMapping("/deleteMessage/{messageId}")
    public String deleteMessage(@PathVariable Integer messageId, Integer userId) {
        userService.deleteMessage(userId, messageId);
        return "redirect:/index";
    }

    @RequestMapping("/createGroup/{name}")
    public ModelAndView createGroup(@PathVariable String name, Integer userId) {
        ModelAndView model = new ModelAndView("redirect:/index");
        Integer id = friendsGroupService.createGroup(name, userId);
        model.addObject("groupId", id);
        return model;
    }

    @RequestMapping("/addFriend/{friendId}")
    public ModelAndView addFriend(@PathVariable String friendId, Integer userId, Integer groupId) {
        ModelAndView model = new ModelAndView("redirect:/index");
        friendsGroupService.addFriend(groupId, new Integer(friendId));
        userUtil.printFriends(userId);
        return model;
    }
}