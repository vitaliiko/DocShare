package com.geekhub.utils;

import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;

public class EventUtil {

    public static Event createEvent(String hashName, User recipient, String text,
                                    String linkText, String linkUtl, User sender) {

        Event event = new Event();
        event.setText(text);
        event.setLinkText(linkText);
        event.setLinkUrl(linkUtl);
        event.setDate(Calendar.getInstance().getTime());
        event.setSenderId(sender.getId());
        event.setSenderName(sender.getFullName());
        event.setRecipient(recipient);
        event.setHashName(hashName == null ? createHashName() : hashName);
        return event;
    }

    public static Event createEvent(User recipient, String text, String linkText, String linkUtl, User sender) {
        return createEvent(null, recipient, text, linkText, linkUtl, sender);
    }

    public static Event createEvent(User recipient, String text, User sender) {
        return createEvent(null, recipient, text, null, null, sender);
    }

    public static List<Event> createEvent(Set<User> recipients, String text, String linkText,
                                          String linkUtl, User sender) {

        List<Event> events = new ArrayList<>();
        recipients.forEach(r -> events.add(createEvent(null, r, text, linkText, linkUtl, sender)));
        return events;
    }

    public static List<Event> createEvent(Set<User> recipients, String text, User sender) {
        List<Event> events = new ArrayList<>();
        recipients.forEach(r -> events.add(createEvent(null, r, text, null, null, sender)));
        return events;
    }

    public static String createHashName() {
        return DigestUtils.md5Hex("" + new Date().getTime());
    }
}
