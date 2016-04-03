package com.geekhub.util;

import com.geekhub.entity.Comment;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import java.util.Calendar;

public class CommentUtil {

    public static Comment createComment(String text, User owner) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setOwner(owner);
        comment.setDate(Calendar.getInstance().getTime());
        return comment;
    }
}
