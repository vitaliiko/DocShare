package com.geekhub.interceptors;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Service
public class MainInterceptor extends HandlerInterceptorAdapter {

    private final List<String> EXCLUSIONS_BY_END = Arrays.asList(
            "/sign_in", "/sign_up", ".css", ".css.map"
    );

    private final List<String> EXCLUSIONS_BY_START = Arrays.asList(
            "/api/documents/link", "/api/directories/link"
    );

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String uri = req.getRequestURI();
        boolean exclusion = EXCLUSIONS_BY_END.stream().anyMatch(uri::endsWith)
                || EXCLUSIONS_BY_START.stream().anyMatch(uri::startsWith);

        if (exclusion || req.getSession().getAttribute("userId") != null) {
            return true;
        }
        resp.sendRedirect("/api/sign_in");
        return false;
    }
}
