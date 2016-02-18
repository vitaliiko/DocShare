package com.geekhub.controller;

import com.geekhub.model.*;
import com.geekhub.util.FriendsGroupUtil;
import com.geekhub.util.MessageUtil;
import com.geekhub.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
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
    public String index(ModelMap model, User user) {
        model.addAttribute("messages", messageService.getMessages());
        model.addAttribute("friends", user.getFriends());
        if (user.getFriendsOf().size() > 0) {
            model.addAttribute("friendsOf", user.getFriendsOf());
        }
        return "index";
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.POST)
    public String index(User user, String text) {
        Message message = new Message();
        message.setText(messageUtil.detectLink(text));
        message.setDate(Calendar.getInstance().getTime());
        userService.addMessage(user, message);
        return "redirect:/index";
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.GET)
    public String login() {
        return "signIn";
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public String login(String login, String password, ModelMap model, HttpSession session) {
        User user = userService.getUserByLogin(login);
        if (user != null && user.getPassword().equals(password)) {
            model.addAttribute("user", user);
            session.setAttribute("user", user);
            return "redirect:/index";
        }
        model.addAttribute("errorMessage", "Wrong login or password");
        return "signIn";
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.GET)
    public String signUp() {
        return "signUp";
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public String signUp(String firstName,
                         String lastName,
                         String login,
                         String password,
                         String confirmPassword,
                         ModelMap model) {
        try {
            User user = userUtil.createUser(login, password, confirmPassword, firstName, lastName);
            userService.saveUser(user);
            return "signIn";
        } catch (Exception e) {
            model.addAttribute("login", login)
                    .addAttribute("firstName", firstName)
                    .addAttribute("lastName", lastName)
                    .addAttribute("errorMessage", e.getMessage());
            return "signUp";
        }
    }

    @RequestMapping("/signOut")
    public String signOut(SessionStatus status, HttpSession session) {
        status.setComplete();
        session.invalidate();
        return "signIn";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile() {
        return "profile";
    }

    @RequestMapping(value = "/changeName", method = RequestMethod.POST)
    public String changeName(User user, HttpServletRequest req, String login, String firstName, String lastName) {
        if (user.getLogin().equals(login) || userService.getUserByLogin(login) == null) {
            user.setLogin(login);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userService.updateUser(user);
            req.setAttribute("message", "Your account updated successfully");
        } else {
            req.setAttribute("errorMessage", "User with such login already exist");
        }
        return "profile";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(User user,
                                 HttpServletRequest req,
                                 String currentPassword,
                                 String newPassword,
                                 String confirmNewPassword) {

        if (!user.getPassword().equals(currentPassword)) {
            req.setAttribute("errorMessage", "Wrong password");
        } else if (!newPassword.equals(confirmNewPassword)) {
            req.setAttribute("errorMessage", "Passwords doesn't match");
        } else {
            user.setPassword(newPassword);
            userService.updateUser(user);
            req.setAttribute("message", "Your password was changed successfully");
        }
        return "profile";
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.GET)
    public String removeAccount() {
        return "confirmRemovingAccount";
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.POST)
    public String removeAccount(SessionStatus status, HttpSession session, User user, HttpServletRequest req, String yes) {
        if (yes != null) {
            userService.deleteUser(user);
            status.setComplete();
            session.invalidate();
            req.setAttribute("message", "Your account removed successfully");
            return "signIn";
        }
        return "profile";
    }

    @RequestMapping(value = "/addFriend/{friend}")
    public String addFriend(User user, @PathVariable String friend, ModelMap model) {
        userService.addFriend(user, friend);
        return "redirect:/index";
    }

    @RequestMapping("/default")
    public String createDefaultUsers() {
        userUtil.addDefaultUsers();
        return "signIn";
    }
}