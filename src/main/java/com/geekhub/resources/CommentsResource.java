package com.geekhub.resources;

import com.geekhub.dto.CommentDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.Comment;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.services.CommentService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentsResource {

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private CommentService commentService;

    @Inject
    private UserService userService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.GET)
    public ResponseEntity<Set<CommentDto>> getComments(@PathVariable Long docId) {
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
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
        Comment comment = commentService.create(text, user, document);
        return ResponseEntity.ok(EntityToDtoConverter.convert(comment));
    }

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.DELETE)
    public ResponseEntity clearComments(@PathVariable long docId) {
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        commentService.deleteCommentsFoDocument(document);
        return ResponseEntity.ok().build();
    }
}
