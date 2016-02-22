package com.geekhub.controller;

import com.geekhub.model.*;
import com.geekhub.util.FriendsGroupUtil;
import com.geekhub.util.MessageUtil;
import com.geekhub.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Calendar;

@Controller
@SessionAttributes("user")
public class ChatController {

    @Autowired private UserService userService;
    @Autowired private UserUtil userUtil;
    @Autowired private MessageService messageService;
    @Autowired private MessageUtil messageUtil;
    @Autowired private FriendsGroupService friendsGroupService;
    @Autowired private FriendsGroupUtil friendsGroupUtil;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView model = new ModelAndView();
        model.addObject("messages", messageService.getMessages());
        model.setViewName("index");
//        model.addAttribute("friends", user.getFriends());
//        if (user.getFriendsOf().size() > 0) {
//            model.addAttribute("friendsOf", user.getFriendsOf());
//        }
        return model;
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.POST)
    public String index(@ModelAttribute User user, @RequestParam String messageText) {
        Message message = new Message();
        message.setText(messageUtil.detectLink(messageText));
        message.setDate(Calendar.getInstance().getTime());
        userService.addMessage(user.getId(), message);
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
            model.addObject("user", user)
                    .setViewName("redirect:/index");
        } else {
            model.addObject("errorMessage", "Wrong login or password");
            model.setViewName("signIn");
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
    public String signOut(HttpSession session) {
        session.invalidate();
        return "signIn";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile() {
        return "profile";
    }

    @RequestMapping(value = "/changeName", method = RequestMethod.POST)
    public ModelAndView changeName(@ModelAttribute User user, String login, String firstName, String lastName) {
        ModelAndView model = new ModelAndView("profile");
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
    public ModelAndView removeAccount(@ModelAttribute User user, String yes, HttpSession session) {
        ModelAndView model = new ModelAndView();
        if (yes != null) {
            userService.deleteUser(user);
            session.invalidate();
            model.addObject("message", "Your account removed successfully")
                    .setViewName("signIn");
            return model;
        }
        model.setViewName("profile");
        return model;
    }

    @RequestMapping(value = "/addFriend/{friend}")
    public String addFriend(@PathVariable String friend, @ModelAttribute User user) {
        userService.addFriend(user, friend);
        return "redirect:/index";
    }

    @RequestMapping("/default")
    public String createDefaultUsers() {
        userUtil.addDefaultUsers();
        return "signIn";
    }

    @RequestMapping("/deleteMessage/{messageId}")
    public String deleteMessage(@PathVariable Integer messageId, @ModelAttribute User user) {
        userService.deleteMessage(user.getId(), messageId);
        return "redirect:/index";
    }

    @RequestMapping("/createGroup")
    public String createGroup() {
        friendsGroupUtil.createDefaultGroup();
        return "redirect:/setOwner";
    }

    @RequestMapping("/setOwner")
    public String setOwner(@ModelAttribute User user) {
        friendsGroupService.setOwner(1, user.getId());
        return "redirect:/index";
    }

    @RequestMapping("/addFriend/{friendId}")
    public String addFriend(@PathVariable String friendId) {
        friendsGroupService.addFriend(1, new Integer(friendId));
//        model.addAttribute("friends", userService.getUserById(user.getId()).getFriendsGroupSet());
        return "redirect:/index";
    }
}