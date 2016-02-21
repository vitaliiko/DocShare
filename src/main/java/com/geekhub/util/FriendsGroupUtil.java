package com.geekhub.util;

import com.geekhub.model.FriendsGroup;
import com.geekhub.model.FriendsGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendsGroupUtil {

    @Autowired private FriendsGroupService friendsGroupService;

    public FriendsGroup createDefaultGroup() {
        FriendsGroup friendsGroup = new FriendsGroup();
        friendsGroup.setName("friends");
        friendsGroupService.saveFriendsGroup(friendsGroup);
        return friendsGroup;
    }
}
