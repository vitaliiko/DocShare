package com.geekhub.utils;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionTools {

    @SafeVarargs
    public static <T> List<T> unionLists(List<T>... lists) {
        List<T> union = new ArrayList<>();
        Arrays.stream(lists)
                .filter(list -> !CollectionUtils.isEmpty(list))
                .distinct().forEach(union::addAll);
        return union;
    }

    public static <T> T getDifferenceObject(List<T> list1, List<T> list2) {
        list1.removeAll(list2);
        return list1.get(0);
    }

    public static List<User> filterUserList(List<User> userList, List<Long> filterList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .filter(u -> filterList.contains(u.getId()))
                .collect(Collectors.toList());
    }

    public static List<FriendsGroup> filterGroupList(List<FriendsGroup> userList, List<Long> filterList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .filter(g -> filterList.contains(g.getId()))
                .collect(Collectors.toList());
    }
}
