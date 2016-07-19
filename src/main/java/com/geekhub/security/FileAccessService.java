package com.geekhub.security;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.function.BiPredicate;

@Service
public class FileAccessService {

    private static FriendGroupToDocumentRelationService staticFriendGroupToDocumentRelationService;
    private static UserToDocumentRelationService staticUserToDocumentRelationService;

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @Inject
    private FriendGroupToDocumentRelationService friendGroupToDocumentRelationService;

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    @PostConstruct
    public void init() {
        staticFriendGroupToDocumentRelationService = friendGroupToDocumentRelationService;
        staticUserToDocumentRelationService = userToDocumentRelationService;
    }


    public static final BiPredicate<User, UserDocument> DOCUMENT_OWNER = (u, d) -> {
        FileRelationType relationType = getUserToDocumentRelationType(d.getId(), u.getId());
        return relationType != null
                && relationType == FileRelationType.OWNER
                && d.getDocumentStatus() == DocumentStatus.ACTUAL;
    };

    public static final BiPredicate<User, UserDocument> REMOVED_DOCUMENT_OWNER = (u, d) -> {
        FileRelationType relationType = getUserToDocumentRelationType(d.getId(), u.getId());
        return relationType != null
                && relationType == FileRelationType.OWNER
                && d.getDocumentStatus() == DocumentStatus.REMOVED;
    };

    public static final BiPredicate<User, UserDocument> DOCUMENT_READER = (u, d) -> {
        FileRelationType relationType = getUserToDocumentRelationType(d.getId(), u.getId());
        return d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != null || isInReaderOrEditorGroups(u, d));
    };

    public static final BiPredicate<User, UserDocument> DOCUMENT_EDITOR = (u, d) -> {
        FileRelationType relationType = getUserToDocumentRelationType(d.getId(), u.getId());
        return relationType != null
                && d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != FileRelationType.READER || isInEditorGroups(u, d));
    };


    public <T> boolean permitAccess(T document, User user, BiPredicate<User, T> predicate) {
        return predicate.test(user, document);
    }

    private static FileRelationType getUserToDocumentRelationType(Long documentId, Long userId) {
        UserToDocumentRelation relation = staticUserToDocumentRelationService.getByDocumentIdAndUserId(documentId, userId);
        if (relation == null) {
            return null;
        }
        return relation.getFileRelationType();
    }

    private static boolean isInReaderOrEditorGroups(User user, UserDocument document) {
        List<FileRelationType> relations = staticFriendGroupToDocumentRelationService
                .getAllRelationsByDocumentIdAndUserId(document.getId(), user);
        return relations.size() > 0;
    }

    private static boolean isInEditorGroups(User user, UserDocument document) {
        List<FileRelationType> relations = staticFriendGroupToDocumentRelationService
                .getAllRelationsByDocumentIdAndUserId(document.getId(), user);
        return relations.contains(FileRelationType.EDITOR);
    }
}