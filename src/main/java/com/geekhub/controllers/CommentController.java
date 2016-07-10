package com.geekhub.controllers;

import com.geekhub.dto.CommentDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.Comment;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.CommentService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
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
        if (!documentAccessService.canRead(document, user)
                || document.getAbilityToComment() == AbilityToCommentDocument.DISABLE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Set<Comment> commentSet = document.getComments();
        Set<CommentDto> commentDtos = commentSet.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toCollection(TreeSet::new));
        return ResponseEntity.ok(commentDtos);
    }

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.POST)
    public ResponseEntity<CommentDto> addComment(@PathVariable long docId, @RequestParam String text, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessService.canRead(document, user)
                && document.getAbilityToComment() == AbilityToCommentDocument.ENABLE
                && !text.isEmpty()) {
            Comment comment = commentService.create(text, user, document);
            return ResponseEntity.ok(EntityToDtoConverter.convert(comment));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.DELETE)
    public ResponseEntity clearComments(@PathVariable long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        if (!documentAccessService.isOwner(document, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        commentService.deleteCommentsFoDocument(document);
        return ResponseEntity.ok().build();
    }
}
