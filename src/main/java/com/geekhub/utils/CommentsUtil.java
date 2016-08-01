package com.geekhub.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentsUtil {

    public static final Pattern URL_PATTERN = Pattern.compile("((https?|ftp):(//)+[\\w\\d:#@%/;$()~_?\\\\+-=.&]*)");
    public static final int MAX_URL_LENGTH = 50;

    public static String detectURLs(String commentText) {
        List<String> urls = new ArrayList<>();
        Matcher match = URL_PATTERN.matcher(commentText);

        while (match.find()) {
            urls.add(match.group());
        }

        if (urls.size() > 0) {
            return replaceURLs(commentText, urls);
        }

        return commentText;
    }

    private static String replaceURLs(String commentText, List<String> urls) {
        String resultText = "";
        for (String s : urls) {
            String shortURL = shortenURL(s);
            String url = "<a href=\"" + s + "\">" + shortURL + "</a>";
            int startUrlIndex = commentText.indexOf(s);
            commentText = StringUtils.replaceOnce(commentText, s, url);
            resultText += commentText.substring(0, startUrlIndex + url.length());
            commentText = commentText.substring(startUrlIndex + url.length());
        }
        resultText += commentText;
        return resultText;
    }

    private static String shortenURL(String stringUrl) {
        if (stringUrl.length() <= 50) {
            return stringUrl;
        }

        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            return stringUrl;
        }

        String protocol = url.getProtocol() + "://";
        String host = url.getHost();
        stringUrl = stringUrl.substring(stringUrl.indexOf(host) + host.length());

        int slashIndex = stringUrl.lastIndexOf("/");
        String resultURL = protocol + host + "/..." + stringUrl.substring(slashIndex);
        if (resultURL.length() > MAX_URL_LENGTH) {
            resultURL = protocol + host + "/.../..." + stringUrl.substring(stringUrl.length() - 10);
        }
        return resultURL;
    }
}
