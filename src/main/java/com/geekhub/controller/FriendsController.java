package com.geekhub.controller;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserService;
import com.geekhub.util.EntityToDtoConverter;
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
    private FriendsGroupService friendsGroupService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping("/view")
    public ModelAndView friends(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<FriendsGroup> groupSet = userService.getAllFriendsGroups(userId);
        Map<User, List<FriendsGroup>> friendsMap = userService.getFriendsGroupsMap(userId);
        ModelAndView model = new ModelAndView("friends");
        model.addObject("friends", friendsMap);
        model.addObject("groups", groupSet);
        return model;
    }

    @RequestMapping("/create_group")
    public Long createGroup(String groupName, @RequestParam("friends[]") Long[] friends, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = new FriendsGroup();
        group.setOwner(user);
        group.setName(groupName);
        group.setFriends(userService.getSetByIds(friends));
        return friendsGroupService.save(group);
    }

    @RequestMapping("/update_group")
    @ResponseStatus(HttpStatus.OK)
    public void updateGroup(@RequestParam("friends[]") Long[] friends,
                            Long groupId, String groupName, HttpSession session) {

        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            group.setName(groupName);
            group.setFriends(userService.getSetByIds(friends));
            friendsGroupService.update(group);
        }
    }

    @RequestMapping("/get_group")
    public FriendsGroupDto getGroup(Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            return EntityToDtoConverter.convert(group);
        }
        return null;
    }

    @RequestMapping("/get_friends")
    public Set<User> getFriends(HttpSession session) {
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

    @RequestMapping("/delete_group")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGroup(Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            friendsGroupService.delete(group);
        }
    }
}
