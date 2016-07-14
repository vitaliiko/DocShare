package com.geekhub.entities;

import com.geekhub.entities.enums.EventStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
@EqualsAndHashCode(of = "id")
public class Event implements Comparable<Event> {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column
    @Getter @Setter
    private String hashName;

    @Column
    @Getter @Setter
    private String senderName;

    @Column
    @Getter @Setter
    private long senderId;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    @Getter @Setter
    private User recipient;

    @Column
    @Getter @Setter
    private String text;

    @Column
    @Getter @Setter
    private String linkText;

    @Column
    @Getter @Setter
    private String linkUrl;

    @Column
    @Getter @Setter
    private Date date;

    @Column(name = "eventStatus")
    @Enumerated(EnumType.ORDINAL)
    @Getter @Setter
    private EventStatus eventStatus = EventStatus.UNREAD;

    @Override
    public int compareTo(Event o) {
        return o.getDate().compareTo(this.getDate());
    }

}
