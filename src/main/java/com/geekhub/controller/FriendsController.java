package com.geekhub.controller;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.service.UserService;
import com.geekhub.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/friends")
@SuppressWarnings("unchecked")
public class FriendsController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtil userUtil;

    @RequestMapping("/view")
    public ModelAndView friends(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<FriendsGroup> groupSet = userService.getFriendsGroups(userId);
        Map<User, List<FriendsGroup>> friendsMap = userUtil.getFriendsWithGroups(userId);
        ModelAndView model = new ModelAndView("pages/friends");
        model.addObject("friends", friendsMap);
        model.addObject("groups", groupSet);
        return model;
    }

    @RequestMapping("/create_group")
    public ModelAndView create(HttpSession session, String groupName) {
        userService.addFriendsGroup((Long) session.getAttribute("userId"), groupName);
        return new ModelAndView("redirect:/friends/view");
    }
}
