package com.geekhub.entity;

import com.geekhub.entity.enums.EventStatus;
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
public class Event implements Comparable<Event> {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String senderName;

    @Column
    private long senderId;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column
    private String text;

    @Column
    private String link;

    @Column
    private Date date;

    @Column(name = "eventStatus")
    @Enumerated(EnumType.ORDINAL)
    private EventStatus eventStatus = EventStatus.UNREAD;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    @Override
    public int compareTo(Event o) {
        return this.getDate().compareTo(o.getDate());
    }
}
