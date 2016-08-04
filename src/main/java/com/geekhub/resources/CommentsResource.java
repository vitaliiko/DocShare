package com.geekhub.resources;

import com.geekhub.dto.CommentDto;
import com.geekhub.dto.CommentTextDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.Comment;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.exceptions.FileAccessException;
import com.geekhub.services.CommentService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;

import java.io.IOException;
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
        Set<CommentDto> commentDtos = getCommentDtos(document);
        return ResponseEntity.ok(commentDtos);
    }

    @RequestMapping(value = "/links/documents/comments", method = RequestMethod.GET)
    public ResponseEntity<Set<CommentDto>> getCommentsByLink(@RequestParam String token) throws FileAccessException {
        UserDocument document = userDocumentService.getDocumentWithCommentsByToken(token);
        Set<CommentDto> commentDtos = getCommentDtos(document);
        return ResponseEntity.ok(commentDtos);
    }

    private Set<CommentDto> getCommentDtos(UserDocument document) {
        Set<Comment> commentSet = document.getComments();
        return commentSet.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.POST)
    public ResponseEntity<CommentDto> addComment(@PathVariable Long docId, @RequestBody CommentTextDto text,
                                                 HttpSession session) throws IOException {

        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        Comment comment = commentService.create(text.getText(), user, document);
        return ResponseEntity.ok(EntityToDtoConverter.convert(comment));
    }

    @RequestMapping(value = "/links/documents/comments", method = RequestMethod.POST)
    public ResponseEntity<CommentDto> addCommentByLink(@RequestBody CommentTextDto textDto,
                                                       HttpSession session) throws IOException, FileAccessException {

        if (textDto.getText().length() > 512) {
            throw new IOException("Message is too long");
        }
        Long userId = (Long) session.getAttribute("userId");
        UserDocument document = userDocumentService.getDocumentWithCommentsByToken(textDto.getToken());
        Comment comment;
        if (userId == null) {
            comment = commentService.create(textDto.getText(), document);
        } else {
            User user = getUserFromSession(session);
            comment = commentService.create(textDto.getText(), user, document);
        }
        return ResponseEntity.ok(EntityToDtoConverter.convert(comment));
    }

    @RequestMapping(value = "/documents/{docId}/comments", method = RequestMethod.DELETE)
    public ResponseEntity clearComments(@PathVariable Long docId) {
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        commentService.deleteCommentsFoDocument(document);
        return ResponseEntity.ok().build();
    }
}
