package com.geekhub.services;

import com.geekhub.entities.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService extends EntityService<Comment, Long> {

}
