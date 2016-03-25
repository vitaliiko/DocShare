package com.geekhub.util;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class FriendsGroupUtil {

    @Autowired
    private FriendsGroupService friendsGroupService;

    @Autowired
    private UserService userService;

    public void updateGroup(Long userId, Long groupId, String groupName, Long[] friendsIds) throws HibernateException {
        User user = userService.getById(userId);
        FriendsGroup group = friendsGroupService.getById(groupId);
        if (!group.getName().equals(groupName) && friendsGroupService.getFriendsGroups(user, groupName).size() == 0) {
            group.setName(groupName);
        } else {
            throw new HibernateException("Friends group with such name already exist");
        }
        Set<User> friendsSet = new HashSet<>();
        Arrays.stream(friendsIds).forEach(id -> friendsSet.add(userService.getById(id)));
        group.setFriends(friendsSet);
        friendsGroupService.update(group);
    }
}
