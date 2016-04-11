package com.geekhub.controllers;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

public class MainInterceptor extends HandlerInterceptorAdapter {

    private final List<String> EXCLUSIONS = Arrays.asList(
            "/sign_in", "/sign_up", ".css", ".css.map"
    );

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String uri = req.getRequestURI();
        boolean exclusion = EXCLUSIONS.stream()
                .anyMatch(uri::endsWith);

        if (exclusion || req.getSession().getAttribute("userId") != null) {
            return true;
        } else {
            resp.sendRedirect("/main/sign_in");
        }
        return false;
    }
}
