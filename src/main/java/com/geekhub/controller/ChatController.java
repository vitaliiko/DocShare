package com.geekhub.controller;

import com.geekhub.model.Message;
import com.geekhub.model.MessageService;
import com.geekhub.model.User;
import com.geekhub.model.UserService;
import com.geekhub.util.MessageUtil;
import com.geekhub.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;

@Controller
@RequestMapping("/")
public class ChatController {

    @Autowired private UserService userService;
    @Autowired private UserUtil userUtil;
    @Autowired private MessageService messageService;
    @Autowired private MessageUtil messageUtil;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index(HttpServletRequest req) {
        req.setAttribute("messages", messageService.getMessages());
        return "index";
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.POST)
    public String index(HttpSession session, String text) {
        Message message = new Message();
        message.setText(messageUtil.detectLink(text));
        message.setUser((User) session.getAttribute("user"));
        message.setDate(Calendar.getInstance().getTime());
        messageService.saveMessage(message);
        return "redirect:/index";
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.GET)
    public String login() {
        return "signIn";
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public String login(String login, String password, HttpSession session, ModelMap model) {
        User user = userService.getUserByLogin(login);
        if (user != null && user.getPassword().equals(password)) {
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

        model.addAttribute("login", login)
                .addAttribute("firstName", firstName)
                .addAttribute("lastName", lastName);

        if (!userUtil.validateUser(login, password, confirmPassword, model)) {
            return "signUp";
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userService.saveUser(user);

        return "signIn";
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
    public String changeName(HttpSession session, HttpServletRequest req, String login, String firstName, String lastName) {
        User user = (User) session.getAttribute("user");
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
    public String changePassword(HttpSession session,
                                 HttpServletRequest req,
                                 String currentPassword,
                                 String newPassword,
                                 String confirmNewPassword) {

        User user = (User) session.getAttribute("user");
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
    public String removeAccount(HttpSession session, HttpServletRequest req, String yes) {
        if (yes != null) {
            User user = (User) session.getAttribute("user");
            userService.deleteUser(user);
            session.invalidate();
            req.setAttribute("message", "Your account removed successfully");
            return "signIn";
        }
        return "profile";
    }
}