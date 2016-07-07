package com.geekhub.controllers;

import com.geekhub.dto.EventDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.Event;
import com.geekhub.entities.User;
import com.geekhub.services.EventService;
import com.geekhub.services.UserService;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpSession;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/event")
public class EventController {

    @Inject
    private EventService eventService;

    @Inject
    private UserService userService;

    @RequestMapping("/browse")
    public ModelAndView browseEvents(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        Set<Event> events = new HashSet<>(eventService.getAllByRecipient(user));
        Set<EventDto> eventDtoSet = new TreeSet<>();
        events.forEach(e -> eventDtoSet.add(EntityToDtoConverter.convert(e)));
        eventService.setReadStatus(events);

        ModelAndView model = new ModelAndView("events");
        model.addObject("events", eventDtoSet);
        if (events.size() == 0) {
            model.addObject("message", "You have not events yet");
        }
        return model;
    }

    @RequestMapping("/clear")
    public ModelAndView clearEventsHistory(HttpSession session) {
        eventService.clearEvents((Long) session.getAttribute("userId"));
        return new ModelAndView("events", "message" , "You have not events yet");
    }

    @RequestMapping(value = "/get_unread_events_count", method = RequestMethod.GET)
    public long getUnreadEventsCount(HttpSession session) {
        return eventService.getUnreadCount((Long) session.getAttribute("userId"));
    }
}
