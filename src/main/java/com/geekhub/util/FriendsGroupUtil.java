package com.geekhub.util;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.UserService;

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


}
