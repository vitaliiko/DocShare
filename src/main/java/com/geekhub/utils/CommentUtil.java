package com.geekhub.utils;

import com.geekhub.entities.Comment;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import java.util.Calendar;

public class CommentUtil {

    public static Comment createComment(String text, User owner, UserDocument document) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setOwner(owner);
        comment.setUserDocument(document);
        comment.setDate(Calendar.getInstance().getTime());
        return comment;
    }
}
