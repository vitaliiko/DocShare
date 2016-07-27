package com.geekhub.interceptors;

import com.geekhub.entities.User;
import com.geekhub.interceptors.utils.RequestURL;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.BiPredicate;

public abstract class AccessInterceptor<T> extends HandlerInterceptorAdapter {

    private Map<RequestURL, BiPredicate<User, T>> predicateMap = new HashedMap();

    public void addPredicate(RequestURL url, BiPredicate<User, T> predicate) {
        predicateMap.put(url, predicate);
    }

    public BiPredicate<User, T> getPredicate(RequestURL url) {
        return predicateMap.get(url);
    }

    @Override
    public abstract boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
            throws Exception;

    public abstract boolean permitAccess(Long objectId, Long userId, RequestURL url);
}
