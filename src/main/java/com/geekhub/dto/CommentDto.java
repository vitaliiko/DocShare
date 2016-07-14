package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class CommentDto implements Comparable<CommentDto> {

    @Getter @Setter
    private String text;

    @Getter @Setter
    private String date;

    @Getter @Setter
    private String senderName;

    @Override
    public int compareTo(CommentDto o) {
        return this.getDate().compareTo(o.getDate());
    }

}
