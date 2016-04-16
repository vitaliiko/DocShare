package com.geekhub.entities;

import com.geekhub.entities.enums.EventStatus;
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
    private String hashName;

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
    private String linkText;

    @Column
    private String linkUrl;

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

    public String getHashName() {
        return hashName;
    }

    public void setHashName(String hashName) {
        this.hashName = hashName;
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

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
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
        return o.getDate().compareTo(this.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return senderId == event.senderId
                && id.equals(event.id)
                && senderName.equals(event.senderName)
                && recipient.equals(event.recipient)
                && text.equals(event.text)
                && date.equals(event.date);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + senderName.hashCode();
        result = 31 * result + (int) (senderId ^ (senderId >>> 32));
        result = 31 * result + recipient.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }
}
