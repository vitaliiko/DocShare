package com.geekhub.util;

import com.geekhub.service.MessageService;
import com.geekhub.service.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class MessageUtil {

    @Autowired private MessageService messageService;

    private Pattern utlPattern = Pattern.compile("^(http[s]?://)(www\\.)?[a-zA-Z0-9_/?#$\\.\\-\\+=&]+[a-zA-Z]{2,5}");

    public String detectLink(String message) {
        String messageWithUrl = "";
        for (String n : message.split("\r\n")) {
            String lineWithUrl = "";
            for (String m : n.split(" ")) {
                if (utlPattern.matcher(m).matches()) {
                    m = "<a href=\"" + m + "\">" + (m.length() > 50 ? m.substring(0, 49) + "..." : m) + "</a>";
                }
                lineWithUrl += m + " ";
            }
            messageWithUrl += lineWithUrl + "<br>";
        }
        return messageWithUrl.trim();
    }
}
