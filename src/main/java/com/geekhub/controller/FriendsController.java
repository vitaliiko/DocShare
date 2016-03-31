package com.geekhub.controller;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserService;
import com.geekhub.util.UserUtil;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
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

    @Autowired
    private FriendsGroupService friendsGroupService;

    @RequestMapping("/view")
    public ModelAndView friends(HttpSession session) throws HibernateException {
        Long userId = (Long) session.getAttribute("userId");
        List<FriendsGroup> groupSet = userService.getAllFriendsGroups(userId);
        Map<User, List<FriendsGroup>> friendsMap = userService.getFriendsGroupsMap(userId);
        ModelAndView model = new ModelAndView("friends");
        model.addObject("friends", friendsMap);
        model.addObject("groups", groupSet);
        return model;
    }

    @RequestMapping("/create_group")
    @ResponseStatus(HttpStatus.OK)
    public void createGroup(HttpSession session, String groupName, @RequestParam("friends[]") Long[] friends)
            throws HibernateException {
        friendsGroupService.addFriendsGroup((Long) session.getAttribute("userId"), groupName, friends);
    }

    @RequestMapping("/update_group")
    @ResponseStatus(HttpStatus.OK)
    public void updateGroup(Long groupId, String groupName, @RequestParam("friends[]") Long[] friends, HttpSession session)
            throws HibernateException {
        friendsGroupService.update((Long) session.getAttribute("userId"), groupId, groupName, friends);
    }

    @RequestMapping("/get_group")
    public FriendsGroup getGroup(String groupName, HttpSession session) throws HibernateException {
        groupName = groupName.trim();
        return userService.getFriendsGroupByName((Long) session.getAttribute("userId"), groupName);
    }

    @RequestMapping("/get_friends")
    public Set<User> getFriends(HttpSession session) throws HibernateException {
        return userService.getFriends((Long) session.getAttribute("userId"));
    }

    @RequestMapping("/add_friend")
    public ModelAndView addFriend(Long friendId, HttpSession session) throws HibernateException {
        userService.addFriend((Long) session.getAttribute("userId"), friendId);
        return new ModelAndView("redirect:/friends/view");
    }

    @RequestMapping("/delete_friend")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(Long friendId, HttpSession session) throws HibernateException {
        userService.deleteFriend((Long) session.getAttribute("userId"), friendId);
    }

    @RequestMapping("/delete_group")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGroup(Long groupId) throws HibernateException {
        friendsGroupService.delete(groupId);
    }

    @RequestMapping("/default_users")
    public ModelAndView createDefaultUsers() {
        userUtil.addDefaultUsers();
        return new ModelAndView("redirect:/main/sign_in");
    }
}
