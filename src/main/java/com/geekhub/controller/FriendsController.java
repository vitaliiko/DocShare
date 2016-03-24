package com.geekhub.controller;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.service.UserService;
import com.geekhub.util.UserUtil;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/friends")
@SuppressWarnings("unchecked")
public class FriendsController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtil userUtil;

    @RequestMapping("/view")
    public ModelAndView friends(HttpSession session) throws HibernateException {
        Long userId = (Long) session.getAttribute("userId");
        List<FriendsGroup> groupSet = userService.getFriendsGroups(userId);
        Map<User, List<FriendsGroup>> friendsMap = userUtil.getFriendsWithGroups(userId);
        ModelAndView model = new ModelAndView("pages/friends");
        model.addObject("friends", friendsMap);
        model.addObject("groups", groupSet);
        return model;
    }

    @RequestMapping("/create_group")
    public ModelAndView create(HttpSession session, String groupName, @RequestParam Long[] friends) throws HibernateException {
        userService.addFriendsGroup((Long) session.getAttribute("userId"), groupName, Arrays.asList(friends));
        return new ModelAndView("redirect:/friends/view");
    }

    @RequestMapping("/get_group")
    public FriendsGroup getGroup(String groupName, HttpSession session) throws HibernateException {
        groupName = groupName.trim();
        return userService.getFriendsGroup((Long) session.getAttribute("userId"), groupName);
    }

    @RequestMapping("/get_friends")
    public Set<User> getFriends(HttpSession session) throws HibernateException {
        return userService.getFriends((Long) session.getAttribute("userId"));
    }

    @RequestMapping("/add_friend")
    public ModelAndView addFriend(Long friendId, HttpSession session) {
        userService.addFriend((Long) session.getAttribute("userId"), friendId);
        return new ModelAndView("redirect:/friends/view");
    }

    @RequestMapping("/delete_friend")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(Long friendId, HttpSession session) {
        userService.deleteFriend((Long) session.getAttribute("userId"), friendId);
    }
}
