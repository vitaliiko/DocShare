package com.geekhub.controllers;

import com.geekhub.dto.UserDto;
import com.geekhub.entities.Event;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.services.impl.EventSendingService;
import com.geekhub.services.EventService;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;

import java.util.*;
import javax.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")
public class FriendsController {

    @Inject
    private UserService userService;

    @Inject
    private FriendsGroupService friendsGroupService;

    @Inject
    private EventService eventService;

    @Inject
    private EventSendingService eventSendingService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping(value = "/friends", method = RequestMethod.GET)
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

    @RequestMapping(value = "/friend-groups", method = RequestMethod.POST)
    public ResponseEntity<Long> createGroup(@RequestParam(value = "friends[]", required = false) Long[] friends,
                                            @RequestParam String groupName,
                                            HttpSession session) {

        long groupId = 0;
        User user = getUserFromSession(session);
        if (groupName == null || groupName.isEmpty()) {
            return new ResponseEntity<>(groupId, HttpStatus.BAD_REQUEST);
        }
        FriendsGroup group = friendsGroupService.getByOwnerAndName(user, groupName);
        if (group == null) {
            group = new FriendsGroup();
            group.setOwner(user);
            group.setName(groupName);
            if (friends != null) {
                group.setFriends(userService.getSetByIds(friends));
            }
            groupId = friendsGroupService.save(group);
            return new ResponseEntity<>(groupId, HttpStatus.OK);
        }
        return new ResponseEntity<>(groupId, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/friend-groups/{groupId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateGroup(@PathVariable Long groupId,
                                            @RequestParam(value = "friends[]", required = false) Long[] friendIds,
                                            @RequestParam String groupName,
                                            HttpSession session) {

        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);

        if (group == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!group.getName().equals(groupName) && friendsGroupService.getByOwnerAndName(user, groupName) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (group.getOwner().equals(user)) {
            Set<User> membersSet = new HashSet<>(group.getFriends());
            Set<User> newMembersSet = null;
            group.setName(groupName);
            if (friendIds != null) {
                newMembersSet = userService.getSetByIds(friendIds);
                group.setFriends(newMembersSet);
            } else {
                group.getFriends().clear();
            }
            friendsGroupService.update(group);

            eventSendingService.sendShareEvent(user, group, membersSet, newMembersSet);
        }
        return null;
    }

    @RequestMapping(value = "/friend-groups/{groupId}", method = RequestMethod.GET)
    public ResponseEntity<FriendsGroupDto> getGroup(@PathVariable Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            return new ResponseEntity<>(EntityToDtoConverter.convert(group), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/friends/{friendId}/add/{eventHash}", method = RequestMethod.POST)
    public ModelAndView addFriend(@PathVariable Long friendId,
                                  @PathVariable String eventHash,
                                  HttpSession session) {

        User user = userService.getById((Long) session.getAttribute("userId"));
        User friend = userService.getById(friendId);

        Event event = eventService.getByHashName(eventHash);
        if (event.getSenderId() == friendId
                && Objects.equals(event.getRecipient().getId(), user.getId())
                && !userService.getFriends(user.getId()).contains(friend)) {
            Long userId = (Long) session.getAttribute("userId");
            userService.addFriend(userId, friendId);
            userService.addFriend(friendId, userId);

            eventSendingService.sendAddToFriendEvent(user, friend);
        }
        return new ModelAndView("redirect:/api/friends/");
    }

    @RequestMapping(value = "/friends/{friendId}/request-to-friend", method = RequestMethod.POST)
    public ModelAndView sendToFriendEvent(@PathVariable Long friendId, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        User friend = userService.getById(friendId);

        if (!userService.areFriends(user.getId(), friend)) {
            eventSendingService.sendToFriendRequestEvent(user, friend);
        }
        return new ModelAndView("redirect:/main/search");
    }

    @RequestMapping(value = "/friends/{friendId}", method = RequestMethod.DELETE)
    public void deleteFriend(@PathVariable Long friendId, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        User friend = userService.getById(friendId);
        userService.deleteFriend(user.getId(), friend.getId());

        eventSendingService.sendDeleteFromFriendEvent(user, friend);
    }

    @RequestMapping(value = "/friend-groups/{groupId}", method = RequestMethod.DELETE)
    public void deleteGroup(@PathVariable Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            friendsGroupService.delete(group);
        }
    }

    @RequestMapping(value = "/friend-groups", method = RequestMethod.GET)
    public Set<FriendsGroupDto> getFriendsGroups(HttpSession session) {
        User user = getUserFromSession(session);
        List<FriendsGroup> groups = friendsGroupService.getListByOwner(user);
        Set<FriendsGroupDto> groupDtos = new TreeSet<>();
        groups.forEach(g -> groupDtos.add(EntityToDtoConverter.convert(g)));
        return groupDtos;
    }
}
