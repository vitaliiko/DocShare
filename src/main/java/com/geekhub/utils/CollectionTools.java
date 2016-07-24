package com.geekhub.utils;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionTools {

    @SafeVarargs
    public static <T> List<T> unionLists(List<T>... lists) {
        List<T> union = new ArrayList<>();
        Arrays.stream(lists).filter(list -> !CollectionUtils.isEmpty(list)).distinct().forEach(union::addAll);
        return union;
    }
}
