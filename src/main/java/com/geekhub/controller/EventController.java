package com.geekhub.controller;

import com.geekhub.entity.Event;
import com.geekhub.entity.User;
import com.geekhub.service.EventService;
import com.geekhub.service.UserService;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @RequestMapping("/browse")
    public ModelAndView browseEvents(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        Set<Event> events = new TreeSet<>(eventService.getAllByRecipient(user));
        eventService.makeRead(events);

        ModelAndView model = new ModelAndView("events");
        model.addObject("events", events);
        if (events.size() == 0) {
            model.addObject("message", "You have not events yet");
        }
        return model;
    }

    @RequestMapping("/clear")
    public ModelAndView clearEventsHistory(HttpSession session) {
        userService.clearEvents((Long) session.getAttribute("userId"));
        return new ModelAndView("events", "message" , "You have not events yet");
    }
}
