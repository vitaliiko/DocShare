package com.geekhub.resources.utils;

import com.geekhub.dto.FriendGroupDto;
import com.geekhub.dto.UserDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.services.UserService;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelUtil {

    public static ModelAndView prepareModelWithShareTable(User user, UserService service, ModelAndView model) {
        List<FriendsGroup> friendsGroups = service.getAllFriendsGroups(user.getId());
        Set<FriendGroupDto> friendGroupDtoSet = friendsGroups.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toSet());

        List<User> friends = service.getAllFriends(user.getId());
        Set<UserDto> friendsDtoSet = friends.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toSet());

        model.addObject("tableNames", FileControllersUtil.ACCESS_ATTRIBUTES);
        model.addObject("friendsGroups", friendGroupDtoSet);
        model.addObject("friends", friendsDtoSet);
        model.addObject("userLogin", user.getLogin());
        return model;
    }
}
