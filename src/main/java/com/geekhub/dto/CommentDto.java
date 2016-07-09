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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentDto that = (CommentDto) o;

        return text.equals(that.text)
                && date.equals(that.date)
                && senderName.equals(that.senderName);

    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + senderName.hashCode();
        return result;
    }
}
