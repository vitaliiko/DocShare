package com.geekhub.util;

import com.geekhub.model.FriendsGroup;
import com.geekhub.model.FriendsGroupService;
import com.geekhub.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendsGroupUtil {

    @Autowired private FriendsGroupService friendsGroupService;

    public FriendsGroup createDefaultGroup(User owner) {
        FriendsGroup friendsGroup = new FriendsGroup();
        friendsGroup.setName("friends");
        friendsGroup.setOwner(owner);
        friendsGroupService.saveFriendsGroup(friendsGroup);
        return friendsGroup;
    }
}
