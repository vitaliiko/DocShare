package com.geekhub.dto;

public class CommentDto implements Comparable<CommentDto> {

    private String text;
    private String date;
    private String senderName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public int compareTo(CommentDto o) {
        return this.getDate().compareTo(o.getDate());
    }
}
