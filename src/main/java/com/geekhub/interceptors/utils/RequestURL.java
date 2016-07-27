package com.geekhub.interceptors.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@EqualsAndHashCode
public class RequestURL {

    @Getter
    private String uri;

    @Getter
    private HttpMethod method;

    public RequestURL(String uri, HttpMethod method) {
        this.uri = uri;
        this.method = method;
    }

    public RequestURL(String uri, String method) {
        this.uri = uri;
        this.method = HttpMethod.valueOf(method);
    }

    public static RequestURL get(String url) {
        return new RequestURL(url, HttpMethod.GET);
    }

    public static RequestURL post(String url) {
        return new RequestURL(url, HttpMethod.POST);
    }

    public static RequestURL put(String url) {
        return new RequestURL(url, HttpMethod.PUT);
    }

    public static RequestURL delete(String url) {
        return new RequestURL(url, HttpMethod.DELETE);
    }
}
