package com.geekhub.controllers;

import com.geekhub.dto.UserDto;
import com.geekhub.entities.Event;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.services.EventService;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.utils.EventUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private UserDirectoryService userDirectoryService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping("/")
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
    public ResponseEntity<Long> createGroup(@RequestParam(value = "friends[]", required = false) Long[] friends,
                                            String groupName,
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

    @RequestMapping("/update_group")
    public ResponseEntity<Void> updateGroup(@RequestParam(value = "friends[]", required = false) Long[] friends,
                                            Long groupId, String groupName, HttpSession session) {

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
            if (friends != null) {
                newMembersSet = userService.getSetByIds(friends);
                group.setFriends(newMembersSet);
            }
            friendsGroupService.update(group);

            long documentsCount = Long.valueOf(userDocumentService.getCountByFriendsGroup(group));
            long directoriesCount = userDirectoryService.getCountByFriendsGroup(group);
            if (documentsCount != 0 || directoriesCount != 0) {
                String filesCount = "";
                if (documentsCount != 0) {
                    filesCount += documentsCount + " documents";
                }
                if (directoriesCount != 0 && documentsCount != 0) {
                    filesCount += " and ";
                }
                if (directoriesCount != 0) {
                    filesCount += directoriesCount + " directories";
                }
                if (newMembersSet != null) {
                    newMembersSet.removeAll(membersSet);
                    String eventText = "User " + user.getFullName() + " has shared " + filesCount;
                    eventService.save(EventUtil.createEvent(newMembersSet, eventText, user));
                }
            }
        }
        return null;
    }

    @RequestMapping("/get_group")
    public ResponseEntity<FriendsGroupDto> getGroup(Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            return new ResponseEntity<>(EntityToDtoConverter.convert(group), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        if (event.getSenderId() == friendId && !userService.getFriends(user.getId()).contains(friend)) {
            Long userId = (Long) session.getAttribute("userId");
            userService.addFriend(userId, friendId);
            userService.addFriend(friendId, userId);

            String eventText = "User " + user.getFullName() + " confirm your request.";
            String eventLinkText = "Friends";
            String eventLinkUrl = "/friends/";

            eventService.save(EventUtil.createEvent(friend, eventText, eventLinkText, eventLinkUrl, user));
        }
        return new ModelAndView("redirect:/friends/");
    }

    @RequestMapping("send_to_friend_event")
    public ModelAndView sendToFriendEvent(Long friendId, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        User friend = userService.getById(friendId);

        if (!userService.areFriends(user.getId(), friend)) {
            String eventHashName = EventUtil.createHashName();
            String eventText = "User " + user.getFullName() + " wants to add you as a friend.";
            String eventLinkText = "Confirm";
            String eventLinkUrl = "/friends/add_friend/" + user.getId() + "/" + eventHashName;

            eventService.save(EventUtil.createEvent(eventHashName, friend, eventText, eventLinkText, eventLinkUrl, user));
        }
        return new ModelAndView("redirect:/main/search");
    }

    @RequestMapping("/delete_friend")
    public void deleteFriend(Long friendId, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        User friend = userService.getById(friendId);
        userService.deleteFriend(user.getId(), friend.getId());

        String eventText = "User " + user.getFullName() + " removed you from friends.";
        eventService.save(EventUtil.createEvent(friend, eventText, user));
    }

    @RequestMapping("/delete_group")
    public void deleteGroup(Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            friendsGroupService.delete(group);
        }
    }

    @RequestMapping("/friend_groups")
    public Set<FriendsGroupDto> getFriendsGroups(HttpSession session) {
        User user = getUserFromSession(session);
        List<FriendsGroup> groups = friendsGroupService.getListByOwner(user);
        Set<FriendsGroupDto> groupDtos = new TreeSet<>();
        groups.forEach(g -> groupDtos.add(EntityToDtoConverter.convert(g)));
        return groupDtos;
    }
}
