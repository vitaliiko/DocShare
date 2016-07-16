package com.geekhub.interceptors;

import com.geekhub.entities.User;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.BiPredicate;

public abstract class AccessInterceptor<T> extends HandlerInterceptorAdapter {

    private Map<String, BiPredicate<T, User>> predicateMap = new HashedMap();

    public Map<String, BiPredicate<T, User>> putPredicate(String url, BiPredicate<T, User> predicate) {
        predicateMap.put(url, predicate);
        return predicateMap;
    }

    public BiPredicate<T, User> getPredicate(String url) {
        return predicateMap.get(url);
    }

    @Override
    public abstract boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
            throws Exception;

    public abstract boolean permitAccess(T object, User user, String url);
}
