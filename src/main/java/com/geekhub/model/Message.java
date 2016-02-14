package com.geekhub.model;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@Table
public class Message extends MappedEntity {
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;
    @Column
    private String text;
    @Column
    private Date date;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
