package com.geekhub.security;

import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.function.Predicate;

@Service
public class FileAccessService {

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    public static final Predicate<UserToDocumentRelation> SHARE = r ->
            r.getFileRelationType() == FileRelationType.OWNER
            && r.getDocument().getDocumentStatus() == DocumentStatus.ACTUAL;

    public <T> boolean permitAccess(T relation, Predicate<T> predicate) {
        return predicate.test(relation);
    }
}
