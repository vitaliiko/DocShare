package com.geekhub.interceptors;

import com.geekhub.entities.User;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AccessInterceptor<R> extends HandlerInterceptorAdapter {

    private Map<String, Predicate<R>> predicateMap = new HashedMap();

    public Map<String, Predicate<R>> addPredicate(String url, Predicate<R> predicate) {
        predicateMap.put(url, predicate);
        return predicateMap;
    }

    public Predicate<R> getPredicate(String url) {
        return predicateMap.get(url);
    }

    @Override
    public abstract boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
            throws Exception;

    public abstract boolean permitAccess(Long objectId, Long userId, String url);
}
