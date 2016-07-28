package com.geekhub.interceptors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoggedInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = Logger.getLogger(LoggedInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        logger.info("*** " + method + ": " + uri);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        String uri = request.getRequestURI();
        int status = response.getStatus();
        String method = request.getMethod();
        logger.info("*** " + method + ": " + uri + ", STATUS: " + status);
    }
}
