package com.geekhub.controllers;

import com.geekhub.dto.UserDto;
import com.geekhub.entities.Event;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.services.EventService;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.utils.EventUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private EventService eventService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping("/view")
    public ModelAndView getFriendsWithGroups(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        List<FriendsGroupDto> groupDtoList = new ArrayList<>();
        userService.getAllFriendsGroups(userId).forEach(g -> groupDtoList.add(EntityToDtoConverter.convert(g)));

        Map<User, List<FriendsGroup>> friendsMap = userService.getFriendsGroupsMap(userId);
        Map<UserDto, List<FriendsGroupDto>> friendsDtoMap = new TreeMap<>();
        for (User user : friendsMap.keySet()) {
            List<FriendsGroupDto> userGroupDtoList = new ArrayList<>();
            friendsMap.get(user).forEach(g -> userGroupDtoList.add(EntityToDtoConverter.convert(g)));
            friendsDtoMap.put(EntityToDtoConverter.convert(user), userGroupDtoList);
        }

        ModelAndView model = new ModelAndView("friends");
        model.addObject("friends", friendsDtoMap);
        model.addObject("groups", groupDtoList);
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
    public Set<UserDto> getFriends(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Set<UserDto> userDtoSet = new HashSet<>();
        userService.getFriends(userId).forEach(u -> userDtoSet.add(EntityToDtoConverter.convert(u)));
        return userDtoSet;
    }

    @RequestMapping("/add_friend/{friendId}/{eventHash}")
    public ModelAndView addFriend(@PathVariable Long friendId,
                                  @PathVariable String eventHash,
                                  HttpSession session) {

        User user = userService.getById((Long) session.getAttribute("userId"));
        User friend = userService.getById(friendId);

        Event event = eventService.getByHashName(eventHash);
        if (event.getSenderId() == friendId) {
            Long userId = (Long) session.getAttribute("userId");
            userService.addFriend(userId, friendId);
            userService.addFriend(friendId, userId);

            String eventText = "User " + user.getFullName() + " confirm your request.";
            String eventLinkText = "Friends";
            String eventLinkUrl = "/friends/view";

            eventService.save(EventUtil.createEvent(friend, eventText, eventLinkText, eventLinkUrl, user));

            return new ModelAndView("redirect:/friends/view");
        }
        return null;
    }

    @RequestMapping("send_to_friend_event")
    public void sendToFriendEvent(Long friendId, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        User friend = userService.getById(friendId);

        String eventHashName = EventUtil.createHashName();
        String eventText = "User " + user.getFullName() + " wants to add you as a friend.";
        String eventLinkText = "Confirm";
        String eventLinkUrl = "/friends/add_friend/" + user.getId() + "/" + eventHashName;

        eventService.save(EventUtil.createEvent(eventHashName, friend, eventText, eventLinkText, eventLinkUrl, user));
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
