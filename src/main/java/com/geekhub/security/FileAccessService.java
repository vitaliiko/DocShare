package com.geekhub.security;

import com.geekhub.entities.*;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
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

    public <T> boolean permitAccess(T document, User user, BiPredicate<User, T> predicate) {
        return predicate.test(user, document);
    }


    protected static FileRelationType getUserToDocumentRelationType(UserDocument document, User user) {
        UserToDocumentRelation relation = staticUserToDocumentRelationService.getByDocumentAndUser(document, user);
        if (relation == null) {
            return null;
        }
        return relation.getFileRelationType();
    }

    protected static FileRelationType getUserToDirectoryRelationType(UserDirectory directory, User user) {
        UserToDirectoryRelation relation = staticUserToDirectoryRelationService.getByDirectoryAndUser(directory, user);
        if (relation == null) {
            return null;
        }
        return relation.getFileRelationType();
    }

    protected static List<FileRelationType> getUserToDirectoriesRelationTypes(List<UserDirectory> directories, User user) {
        return staticUserToDirectoryRelationService.getAllRelationsByDirectoriesAndUser(directories, user);
    }

    protected static List<FileRelationType> getUserToDocumentsRelationTypes(List<UserDocument> documents, User user) {
        return staticUserToDocumentRelationService.getAllRelationsByDocumentsAndUser(documents, user);
    }

    protected static boolean isInReaderOrEditorGroups(User user, UserDocument document) {
        List<FileRelationType> relations = staticFriendGroupToDocumentRelationService
                .getAllRelationsByDocumentIdAndUser(document.getId(), user);
        return relations.size() > 0;
    }

    protected static boolean isInReaderOrEditorGroups(User user, UserDirectory directory) {
        List<FileRelationType> relations = staticFriendGroupToDirectoryRelationService
                .getAllRelationsByDirectoryIdAndUser(directory.getId(), user);
        return relations.size() > 0;
    }

    protected static boolean isInEditorGroups(User user, UserDocument document) {
        List<FileRelationType> relations = staticFriendGroupToDocumentRelationService
                .getAllRelationsByDocumentIdAndUser(document.getId(), user);
        return relations.contains(FileRelationType.EDIT);
    }

    protected static boolean isInEditorGroups(User user, UserDirectory directory) {
        List<FileRelationType> relations = staticFriendGroupToDirectoryRelationService
                .getAllRelationsByDirectoryIdAndUser(directory.getId(), user);
        return relations.contains(FileRelationType.EDIT);
    }
}
