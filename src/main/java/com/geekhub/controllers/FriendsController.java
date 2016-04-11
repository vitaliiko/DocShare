package com.geekhub.controllers;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import java.util.TreeSet;
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
    public Long createGroup(@RequestParam(value = "friends[]", required = false) Long[] friends,
                            String groupName, HttpSession session) {

        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getByOwnerAndName(user, groupName);
        if (group == null) {
            group = new FriendsGroup();
            group.setOwner(user);
            group.setName(groupName);
            if (friends != null) {
                group.setFriends(userService.getSetByIds(friends));
            }
            return friendsGroupService.save(group);
        }
        return null;
    }

    @RequestMapping("/update_group")
    @ResponseStatus(HttpStatus.OK)
    public void updateGroup(@RequestParam(value = "friends[]", required = false) Long[] friends,
                            Long groupId, String groupName, HttpSession session) {

        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            group.setName(groupName);
            if (friends != null) {
                group.setFriends(userService.getSetByIds(friends));
            }
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

    @RequestMapping("/get-friends-groups")
    public Set<FriendsGroupDto> getFriendsGroups(HttpSession session) {
        User user = getUserFromSession(session);
        List<FriendsGroup> groups = friendsGroupService.getListByOwner(user);
        Set<FriendsGroupDto> groupDtos = new TreeSet<>();
        groups.forEach(g -> groupDtos.add(EntityToDtoConverter.convert(g)));
        return groupDtos;
    }
}
