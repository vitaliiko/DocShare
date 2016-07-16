package com.geekhub.security;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.services.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.function.BiPredicate;

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

    public boolean permitAccess(UserDocument document, User user, BiPredicate<UserDocument, User> predicate) {
        return true;
    }

    public boolean permitAccess(UserDirectory directory, User user, BiPredicate<UserDocument, User> predicate) {
        return false;
    }
}
