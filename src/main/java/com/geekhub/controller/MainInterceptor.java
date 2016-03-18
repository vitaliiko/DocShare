package com.geekhub.controller;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

public class MainInterceptor extends HandlerInterceptorAdapter {

    private final List<String> EXCLUSIONS = Arrays.asList(
            "/signIn", "/signUp", "/default", "bootstrap.min.css"
    );

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String uri = req.getRequestURI();
        boolean exclusion = EXCLUSIONS.stream()
                .anyMatch(uri::endsWith);

        if (exclusion || req.getSession().getAttribute("user") != null) {
            return true;
        } else {
            resp.sendRedirect("/signIn");
        }
        return false;
    }
}
