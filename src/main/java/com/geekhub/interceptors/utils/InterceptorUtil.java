package com.geekhub.interceptors.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class InterceptorUtil {

    public static String removeVariablesFromURI(HttpServletRequest req, Map<String, String> pathVariables) {
        String url = req.getRequestURI();
        for (String var : pathVariables.values()) {
            url = url.replace(var, "*");
        }
        return url;
    }
}
