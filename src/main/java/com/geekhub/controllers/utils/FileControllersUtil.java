package com.geekhub.controllers.utils;

import com.geekhub.services.EntityService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileControllersUtil {

    public static final String[] ACCESS_ATTRIBUTES = new String[] {"ALL", "PRIVATE", "PUBLIC", "FOR_FRIENDS"};

    public static <T, S extends EntityService<T, Long>> Set<T> createEntitySet(List<Long> ids, S service) {
        return ids.stream()
                .map(service::getById)
                .collect(Collectors.toSet());
    }
}
