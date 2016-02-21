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

import javax.servlet.http.HttpSession;
import java.util.Calendar;

@Controller
public class ChatController {

    @Autowired private UserService userService;
    @Autowired private UserUtil userUtil;
    @Autowired private MessageService messageService;
    @Autowired private MessageUtil messageUtil;
    @Autowired private FriendsGroupService friendsGroupService;
    @Autowired private FriendsGroupUtil friendsGroupUtil;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index(ModelMap model) {
        model.addAttribute("messages", messageService.getMessages());
//        model.addAttribute("friends", user.getFriends());
//        if (user.getFriendsOf().size() > 0) {
//            model.addAttribute("friendsOf", user.getFriendsOf());
//        }
        return "index";
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.POST)
    public String index(HttpSession session, String text) {
        User user = (User) session.getAttribute("user");
        System.out.println("ID : " + user.getId());
        Message message = new Message();
        message.setText(messageUtil.detectLink(text));
        message.setDate(Calendar.getInstance().getTime());
        userService.addMessage(user.getId(), message);
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
//            model.addAttribute("user", user);
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
            userUtil.validateUser(login, password, confirmPassword);
            User user = new User(login, password, firstName, lastName);
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
    public String signOut(HttpSession session) {
        session.invalidate();
        return "signIn";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile() {
        return "profile";
    }

    @RequestMapping(value = "/changeName", method = RequestMethod.POST)
    public String changeName(ModelMap model, HttpSession session, String login, String firstName, String lastName) {
        User user = (User) session.getAttribute("user");
        if (user.getLogin().equals(login) || userService.getUserByLogin(login) == null) {
            user.setLogin(login);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userService.updateUser(user);
            model.addAttribute("message", "Your account updated successfully");
        } else {
            model.addAttribute("errorMessage", "User with such login already exist");
        }
        return "profile";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(HttpSession session,
                                 ModelMap model,
                                 String currentPassword,
                                 String newPassword,
                                 String confirmNewPassword) {

        User user = (User) session.getAttribute("user");
        if (!user.getPassword().equals(currentPassword)) {
            model.addAttribute("errorMessage", "Wrong password");
        } else if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("errorMessage", "Passwords doesn't match");
        } else {
            user.setPassword(newPassword);
            userService.updateUser(user);
            model.addAttribute("message", "Your password was changed successfully");
        }
        return "profile";
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.GET)
    public String removeAccount() {
        return "confirmRemovingAccount";
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.POST)
    public String removeAccount(HttpSession session, String yes, ModelMap model) {
        User user = (User) session.getAttribute("user");
        if (yes != null) {
            userService.deleteUser(user);
            session.invalidate();
            model.addAttribute("message", "Your account removed successfully");
            return "signIn";
        }
        return "profile";
    }

    @RequestMapping(value = "/addFriend/{friend}")
    public String addFriend(@PathVariable String friend, HttpSession session) {
        userService.addFriend((User) session.getAttribute("user"), friend);
        return "redirect:/index";
    }

    @RequestMapping("/default")
    public String createDefaultUsers() {
        userUtil.addDefaultUsers();
        return "signIn";
    }

    @RequestMapping("/deleteMessage/{messageId}")
    public String deleteMessage(@PathVariable Integer messageId, HttpSession session) {
        userService.deleteMessage(((User) session.getAttribute("user")).getId(), messageId);
        return "redirect:/index";
    }

    @RequestMapping("/createGroup")
    public String createGroup() {
        friendsGroupUtil.createDefaultGroup();
        return "redirect:/setOwner";
    }

    @RequestMapping("/setOwner")
    public String setOwner(HttpSession session) {
        friendsGroupService.setOwner(1, ((User) session.getAttribute("user")).getId());
        return "redirect:/index";
    }

    @RequestMapping("/addFriend/{friendId}")
    public String addFriend(@PathVariable String friendId, ModelMap model, HttpSession session) {
        friendsGroupService.addFriend(1, new Integer(friendId));
//        model.addAttribute("friends", userService.getUserById(user.getId()).getFriendsGroupSet());
        return "redirect:/index";
    }
}