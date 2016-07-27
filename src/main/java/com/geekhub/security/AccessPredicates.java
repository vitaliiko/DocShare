package com.geekhub.security;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.entities.enums.FileRelationType;

import java.util.List;
import java.util.function.BiPredicate;

public class AccessPredicates {

    public static final BiPredicate<User, UserDocument> DOCUMENT_OWNER = (u, d) -> {
        FileRelationType relationType = FileAccessService.getUserToDocumentRelationType(d, u);
        return relationType != null
                && relationType == FileRelationType.OWN
                && d.getDocumentStatus() == DocumentStatus.ACTUAL;
    };

    public static final BiPredicate<User, UserDocument> REMOVED_DOCUMENT_OWNER = (u, d) -> {
        FileRelationType relationType = FileAccessService.getUserToDocumentRelationType(d, u);
        return relationType != null
                && relationType == FileRelationType.OWN
                && d.getDocumentStatus() == DocumentStatus.REMOVED;
    };

    public static final BiPredicate<User, UserDocument> DOCUMENT_READER = (u, d) -> {
        FileRelationType relationType = FileAccessService.getUserToDocumentRelationType(d, u);
        return d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != null
                || d.getDocumentAttribute() == DocumentAttribute.PUBLIC
                || (d.getDocumentAttribute() == DocumentAttribute.FOR_FRIENDS && FileAccessService.isOwnerFriend(u, d))
                || FileAccessService.isInReaderOrEditorGroups(u, d));
    };

    public static final BiPredicate<User, UserDocument> DOCUMENT_EDITOR = (u, d) -> {
        FileRelationType relationType = FileAccessService.getUserToDocumentRelationType(d, u);
        return relationType != null
                && d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != FileRelationType.READ || FileAccessService.isInEditorGroups(u, d));
    };

    public static final BiPredicate<User, UserDocument> DOCUMENT_COMMENTER = (u, d) ->
            DOCUMENT_READER.test(u, d) && d.getAbilityToComment() == AbilityToCommentDocument.ENABLE;

    public static final BiPredicate<User, UserDirectory> DIRECTORY_OWNER = (u, d) -> {
        FileRelationType relationType = FileAccessService.getUserToDirectoryRelationType(d, u);
        return relationType != null
                && relationType == FileRelationType.OWN
                && d.getDocumentStatus() == DocumentStatus.ACTUAL;
    };

    public static final BiPredicate<User, UserDirectory> DIRECTORY_READER = (u, d) -> {
        FileRelationType relationType = FileAccessService.getUserToDirectoryRelationType(d, u);
        return d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != null
                || d.getDocumentAttribute() == DocumentAttribute.PUBLIC
                || (d.getDocumentAttribute() == DocumentAttribute.FOR_FRIENDS && FileAccessService.isOwnerFriend(u, d))
                || FileAccessService.isInReaderOrEditorGroups(u, d));
    };

    public static final BiPredicate<User, UserDirectory> DIRECTORY_EDITOR = (u, d) -> {
        FileRelationType relationType = FileAccessService.getUserToDirectoryRelationType(d, u);
        return d.getDocumentStatus() == DocumentStatus.ACTUAL
                && (relationType != FileRelationType.READ || FileAccessService.isInEditorGroups(u, d));
    };

    public static final BiPredicate<User, List<UserDirectory>> DIRECTORIES_OWNER = (u, directories) -> {
        List<FileRelationType> relationTypes = FileAccessService.getUserToDirectoriesRelationTypes(directories, u);
        return directories.stream().map(UserDirectory::getDocumentStatus).allMatch(s -> s == DocumentStatus.ACTUAL)
                && relationTypes.stream().allMatch(r -> r == FileRelationType.OWN);
    };

    public static final BiPredicate<User, List<UserDocument>> DOCUMENTS_OWNER = (u, documents) -> {
        List<FileRelationType> relationTypes = FileAccessService.getUserToDocumentsRelationTypes(documents, u);
        return documents.stream().map(UserDocument::getDocumentStatus).allMatch(s -> s == DocumentStatus.ACTUAL)
                && relationTypes.stream().allMatch(r -> r == FileRelationType.OWN);
    };
}
