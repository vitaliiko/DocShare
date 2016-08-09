package com.geekhub.loggin;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoggedInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = Logger.getLogger("REST API");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        logger.info(ConsoleConstants.COLOR_RED + method + ConsoleConstants.COLOR_DEFAULT + ": " + uri);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        String uri = request.getRequestURI();
        int status = response.getStatus();
        logger.info(ConsoleConstants.COLOR_YELLOW + status + ConsoleConstants.COLOR_DEFAULT + ": " + uri);
    }
}
