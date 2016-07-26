package com.geekhub.utils;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
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
        if (CollectionUtils.isEmpty(list1)) {
            return null;
        }
        try {
            list1.removeAll(list2);
            if (list1.size() != 1) {
                throw new IOException("File have more then one owner or haven't owner");
            }
            return list1.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public static boolean isAllNumeric(String[]... numericArray) {
        return numericArray != null
                && Arrays.stream(numericArray)
                .filter(arr -> arr != null)
                .flatMap(Arrays::stream)
                .allMatch(StringUtils::isNumeric);
    }
}
