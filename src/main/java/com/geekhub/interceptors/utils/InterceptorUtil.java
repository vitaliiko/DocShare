package com.geekhub.interceptors.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterceptorUtil {

    public static RequestURL createRequestURL(HttpServletRequest req, Map<String, String> pathVariables) {
        String url = req.getRequestURI();
        for (String var : pathVariables.values()) {
            url = url.replace(var, "*");
        }
        return new RequestURL(url, req.getMethod());
    }

    public static String getRequestBody(HttpServletRequest request) {
        StringBuilder body = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (Exception e) {

        }
        return body.toString();
    }

    public static List<String> getStringList(JSONObject jsonObject, String arrayName) {
        List<String> list = new ArrayList<>();
        if (jsonObject.has(arrayName)) {

            JSONArray array = jsonObject.getJSONArray(arrayName);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        }
        return list;
    }
}
