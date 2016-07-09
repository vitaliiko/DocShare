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
import javax.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private CommentService commentService;

    @Inject
    private UserDocumentAccessService documentAccessService;

    @Inject
    private UserService userService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.GET)
    public ResponseEntity<Set<CommentDto>> getComments(@PathVariable long docId, HttpSession session) {
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

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.POST)
    public CommentDto addComment(@PathVariable long docId, @RequestParam String text, HttpSession session) {
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

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.DELETE)
    public void clearComments(@PathVariable long docId, HttpSession session) {
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
