package com.geekhub.security;

import com.geekhub.entities.*;
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

    private static UserToDocumentRelationService staticUserToDocumentRelationService;
    private static UserToDirectoryRelationService staticUserToDirectoryRelationService;
    private static FriendGroupToDocumentRelationService staticFriendGroupToDocumentRelationService;
    private static FriendGroupToDirectoryRelationService staticFriendGroupToDirectoryRelationService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @Inject
    private FriendGroupToDocumentRelationService friendGroupToDocumentRelationService;

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    @Inject
    private FriendGroupToDirectoryRelationService friendGroupToDirectoryRelationService;

    @PostConstruct
    public void init() {
        staticUserToDocumentRelationService = userToDocumentRelationService;
        staticUserToDirectoryRelationService = userToDirectoryRelationService;
        staticFriendGroupToDocumentRelationService = friendGroupToDocumentRelationService;
        staticFriendGroupToDirectoryRelationService = friendGroupToDirectoryRelationService;
    }


    public static final BiPredicate<User, UserDocument> DOCUMENT_OWNER = (u, d) -> {
        FileRelationType relationType = getUserToDocumentRelationType(d.getId(), u.getId());
        return relationType != null
                && relationType == FileRelationType.OWN
                && d.getDocumentStatus() == DocumentStatus.ACTUAL;
    };

    public static final BiPredicate<User, UserDocument> REMOVED_DOCUMENT_OWNER = (u, d) -> {
        FileRelationType relationType = getUserToDocumentRelationType(d.getId(), u.getId());
        return relationType != null
                && relationType == FileRelationType.OWN
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
                && (relationType != FileRelationType.READ || isInEditorGroups(u, d));
    };

    public static final BiPredicate<User, UserDirectory> DIRECTORY_OWNER = (u, d) -> {
        FileRelationType relationType = getUserToDirectoryRelationType(d.getId(), u.getId());
        return relationType != null
                && relationType == FileRelationType.OWN
                && d.getDocumentStatus() == DocumentStatus.ACTUAL;
    };

    public static final BiPredicate<User, UserDirectory> DIRECTORY_READER = (u, d) -> {
        FileRelationType relationType = getUserToDirectoryRelationType(d.getId(), u.getId());
        return d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != null || isInReaderOrEditorGroups(u, d));
    };

    public static final BiPredicate<User, UserDirectory> DIRECTORY_EDITOR = (u, d) -> {
        FileRelationType relationType = getUserToDirectoryRelationType(d.getId(), u.getId());
        return d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != FileRelationType.READ || isInEditorGroups(u, d));
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

    private static FileRelationType getUserToDirectoryRelationType(Long documentId, Long userId) {
        UserToDirectoryRelation relation = staticUserToDirectoryRelationService.getByDirectoryIdAndUserId(documentId, userId);
        if (relation == null) {
            return null;
        }
        return relation.getFileRelationType();
    }

    private static boolean isInReaderOrEditorGroups(User user, UserDocument document) {
        List<FileRelationType> relations = staticFriendGroupToDocumentRelationService
                .getAllRelationsByDocumentIdAndUser(document.getId(), user);
        return relations.size() > 0;
    }

    private static boolean isInReaderOrEditorGroups(User user, UserDirectory directory) {
        List<FileRelationType> relations = staticFriendGroupToDirectoryRelationService
                .getAllRelationsByDirectoryIdAndUser(directory.getId(), user);
        return relations.size() > 0;
    }

    private static boolean isInEditorGroups(User user, UserDocument document) {
        List<FileRelationType> relations = staticFriendGroupToDocumentRelationService
                .getAllRelationsByDocumentIdAndUser(document.getId(), user);
        return relations.contains(FileRelationType.EDIT);
    }

    private static boolean isInEditorGroups(User user, UserDirectory directory) {
        List<FileRelationType> relations = staticFriendGroupToDirectoryRelationService
                .getAllRelationsByDirectoryIdAndUser(directory.getId(), user);
        return relations.contains(FileRelationType.EDIT);
    }
}
