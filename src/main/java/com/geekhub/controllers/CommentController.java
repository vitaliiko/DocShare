package com.geekhub.controllers;

import com.geekhub.dto.CommentDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.Comment;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.exceptions.ResourceNotFoundException;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.CommentService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.utils.CommentUtil;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserDocumentAccessService documentAccessService;

    @Autowired
    private UserService userService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Set<CommentDto>> getComments(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        if (documentAccessService.canRead(document, user)
                && document.getAbilityToComment() == AbilityToCommentDocument.ENABLE) {
            Set<CommentDto> comments = new TreeSet<>();
            document.getComments().forEach(c -> comments.add(EntityToDtoConverter.convert(c)));
            return new ResponseEntity<>(comments, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommentDto addComment(String text, long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessService.canRead(document, user)
                && document.getAbilityToComment() == AbilityToCommentDocument.ENABLE
                && !text.isEmpty()) {
            Comment comment = CommentUtil.createComment(text, user, document);
            commentService.save(comment);
            return EntityToDtoConverter.convert(comment);
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping("/clear")
    public void clearComments(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        if (documentAccessService.isOwner(document, user)) {
            document.getComments().clear();
            userDocumentService.update(document);
        } else {
            throw new ResourceNotFoundException();
        }
    }
}
