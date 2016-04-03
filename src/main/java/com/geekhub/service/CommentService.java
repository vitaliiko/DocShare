package com.geekhub.service;

import com.geekhub.entity.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService extends EntityService<Comment, Long> {

}
