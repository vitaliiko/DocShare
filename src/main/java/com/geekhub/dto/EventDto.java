package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(of = {"text", "date"})
public class EventDto implements Comparable<EventDto> {

    @Getter @Setter
    private String text;

    @Getter @Setter
    private String linkText;

    @Getter @Setter
    private String linkUrl;

    @Getter @Setter
    private LocalDateTime date;

    @Getter @Setter
    private String status;

    @Override
    public int compareTo(EventDto o) {
        return o.getDate().compareTo(this.getDate());
    }

}
