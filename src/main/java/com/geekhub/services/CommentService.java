package com.geekhub.services;

import com.geekhub.entities.Comment;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import org.springframework.stereotype.Service;

@Service
public interface CommentService extends EntityService<Comment, Long> {

    Comment create(String text, User user, UserDocument document);

    void deleteCommentsFoDocument(UserDocument document);

}
