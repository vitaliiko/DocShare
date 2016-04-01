package com.geekhub.controller;

import com.geekhub.entity.User;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.MessageService;
import com.geekhub.service.UserService;
import com.geekhub.util.FriendsGroupUtil;
import com.geekhub.util.MessageUtil;
import com.geekhub.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private FriendsGroupService friendsGroupService;

    @Autowired
    private FriendsGroupUtil friendsGroupUtil;


    @RequestMapping(value = "/signIn", method = RequestMethod.GET)
    public String signIn() {
        return "pages/signIn";
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public ModelAndView signIn(String login, String password, HttpSession session) {
        User user = userService.getByLogin(login);
        ModelAndView model = new ModelAndView();
        if (user != null && user.getPassword().equals(password)) {
            model.setViewName("redirect:/index");
            session.setAttribute("userId", user.getId());
        } else {
            model.addObject("errorMessage", "Wrong login or password")
                    .setViewName("pages/signIn");
        }
        return model;
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.GET)
    public String signUp() {
        return "pages/signUp";
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
            userService.save(user);
            model.setViewName("pages/signIn");
        } catch (Exception e) {
            model.addObject("login", login)
                    .addObject("firstName", firstName)
                    .addObject("lastName", lastName)
                    .addObject("errorMessage", e.getMessage())
                    .setViewName("pages/signUp");
        }
        return model;
    }

    @RequestMapping("/signOut")
    public String signOut(HttpSession session) {
        session.invalidate();
        return "pages/signIn";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("pages/profile");
        model.addObject("login", user.getLogin())
                .addObject("firstName", user.getFirstName())
                .addObject("lastName", user.getLastName());
        return model;
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.GET)
    public String removeAccount() {
        return "confirmRemovingAccount";
    }

    @RequestMapping(value = "/removeAccount", method = RequestMethod.POST)
    public ModelAndView removeAccount(String yes, HttpSession session) {
        ModelAndView model = new ModelAndView();
        if (yes != null) {
            userService.deleteById((Long) session.getAttribute("userId"));
            session.invalidate();
            model.addObject("message", "Your account removed successfully")
                    .setViewName("pages/signIn");
            return model;
        }
        model.setViewName("profile");
        return model;
    }

    @RequestMapping("/default")
    public String createDefaultUsers() {
        userUtil.addDefaultUsers();
        return "redirect:/sign_in";
    }

    @RequestMapping("/deleteMessage/{messageId}")
    public String deleteMessage(@PathVariable Long messageId, HttpSession session) {
        userService.deleteMessage((Long) session.getAttribute("userId"), messageId);
        return "redirect:/index";
    }

    @RequestMapping("/addFriend/{friendId}")
    public ModelAndView addFriend(@PathVariable Long friendId, Long userId, Long groupId) {
        ModelAndView model = new ModelAndView("redirect:/index");
        friendsGroupService.addFriend(groupId, friendId);
        return model;
    }

    @RequestMapping("/defaultUsers")
    public ModelAndView defaultUsers() {
        userUtil.addDefaultUsers();
        return new ModelAndView("redirect:/main/home");
    }
}