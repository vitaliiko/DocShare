package com.geekhub.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentsUtil {

    public static final Pattern URL_PATTERN = Pattern.compile("((https?|ftp|http):(//)+[\\w\\d:#@%/;$()~_?\\\\+-=.&]*)");

    public static String detectURLs(String originalText) {
        String resultText = "";
        String commentText = originalText;
        List<String> urls = new ArrayList<>();
        Matcher match = URL_PATTERN.matcher(commentText);

        while (match.find()) {
            urls.add(match.group());
        }

        for (String s : urls) {
            String url = "<a href=\"" + s + "\">" + s + "</a>";
            int startIndex = commentText.indexOf(s);
            commentText = StringUtils.replaceOnce(commentText, s, url);
            resultText += commentText.substring(0, startIndex + url.length());
            commentText = commentText.substring(startIndex + url.length());
        }

        return resultText;
    }
}
