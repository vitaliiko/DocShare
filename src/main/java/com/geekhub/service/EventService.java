package com.geekhub.service;

import com.geekhub.entity.Event;
import org.springframework.stereotype.Service;

@Service
public interface EventService extends EntityService<Event, Long> {
}
