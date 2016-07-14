package com.geekhub.resources;

import com.geekhub.dto.CreateFriendGroupDto;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.Event;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.dto.FriendGroupDto;
import com.geekhub.services.EventSendingService;
import com.geekhub.services.EventService;
import com.geekhub.services.FriendGroupService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.EntityToDtoConverter;

import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class FriendsResource {

    @Inject
    private UserService userService;

    @Inject
    private FriendGroupService friendGroupService;

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

        List<FriendsGroup> friendsGroups = userService.getAllFriendsGroups(userId);
        List<FriendGroupDto> groupDtoList =
                friendsGroups.stream().map(EntityToDtoConverter::convert).collect(Collectors.toList());

        Map<User, List<FriendsGroup>> friendsMap = userService.getFriendsGroupsMap(userId);
        Map<UserDto, List<FriendGroupDto>> friendsDtoMap = EntityToDtoConverter.convertMap(friendsMap);

        ModelAndView model = new ModelAndView("friends");
        model.addObject("friends", friendsDtoMap);
        model.addObject("groups", groupDtoList);
        return model;
    }

    @RequestMapping(value = "/friend-groups", method = RequestMethod.POST)
    public ResponseEntity<FriendGroupDto> createGroup(@RequestBody CreateFriendGroupDto groupDto,
                                                      HttpSession session) {

        User user = getUserFromSession(session);
        FriendsGroup group = friendGroupService.getByOwnerAndName(user, groupDto.getGroupName());
        if (group != null) {
            return ResponseEntity.badRequest().body(null);
        }
        FriendsGroup newGroup = friendGroupService.create(user, groupDto);
        return ResponseEntity.ok(EntityToDtoConverter.convert(newGroup));
    }

    @RequestMapping(value = "/friend-groups", method = RequestMethod.PUT)
    public ResponseEntity<FriendGroupDto> updateGroup(@RequestBody CreateFriendGroupDto groupDto,
                                                      HttpSession session) {

        User user = getUserFromSession(session);
        FriendsGroup group = friendGroupService.getById(groupDto.getId());

        String groupName = groupDto.getGroupName();
        if (group == null
                || !group.getOwner().equals(user)
                || (!group.getName().equals(groupName) && friendGroupService.getByOwnerAndName(user, groupName) != null)) {
            return ResponseEntity.badRequest().body(null);
        }
        FriendsGroup updatedGroup = friendGroupService.update(user, groupDto);
        return ResponseEntity.ok(EntityToDtoConverter.convert(updatedGroup));
    }

    @RequestMapping(value = "/friend-groups/{groupId}", method = RequestMethod.GET)
    public ResponseEntity<FriendGroupDto> getGroup(@PathVariable Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            return ResponseEntity.ok(EntityToDtoConverter.convert(group));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
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
                && !userService.areFriends(user.getId(), friend)) {
            userService.addFriend(user.getId(), friendId);
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
        return new ModelAndView("redirect:/api/search");
    }

    @RequestMapping(value = "/friends/{friendId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteFriend(@PathVariable Long friendId, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        userService.deleteFriend(user.getId(), friendId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/friend-groups/{groupId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteGroup(@PathVariable Long groupId, HttpSession session) {
        User user = getUserFromSession(session);
        FriendsGroup group = friendGroupService.getById(groupId);
        if (group != null && group.getOwner().equals(user)) {
            friendGroupService.delete(group);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/friend-groups", method = RequestMethod.GET)
    public ResponseEntity<Set<FriendGroupDto>> getFriendsGroups(HttpSession session) {
        User user = getUserFromSession(session);
        List<FriendsGroup> groups = friendGroupService.getListByOwner(user);
        Set<FriendGroupDto> groupDtos = groups.stream().map(EntityToDtoConverter::convert).collect(Collectors.toSet());
        return ResponseEntity.ok(groupDtos);
    }
}
