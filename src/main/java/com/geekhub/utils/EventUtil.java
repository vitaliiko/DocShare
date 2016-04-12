package com.geekhub.utils;

import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class EventUtil {

    public static Event createEvent(User recipient, String text, String linkText, String linkUtl, User sender) {
        Event event = new Event();
        event.setText(text);
        event.setLinkText(linkText);
        event.setLinkUrl(linkUtl);
        event.setDate(Calendar.getInstance().getTime());
        event.setSenderId(sender.getId());
        event.setSenderName(sender.toString());
        event.setRecipient(recipient);
        return event;
    }

    public static Event createEvent(User recipient, String linkUtl, User sender) {
        return createEvent(recipient, null, null, linkUtl, sender);
    }

    public static List<Event> createEvents(Set<User> recipients, String text, String linkText, String linkUtl, User sender) {
        List<Event> events = new ArrayList<>();
        recipients.forEach(r -> events.add(createEvent(r, text, linkText, linkUtl, sender)));
        return events;
    }

    public static List<Event> createEvents(Set<User> recipients, String text, User sender) {
        List<Event> events = new ArrayList<>();
        recipients.forEach(r -> events.add(createEvent(r, text, null, null, sender)));
        return events;
    }
}
