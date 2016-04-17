package com.geekhub.security;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.services.RemovedDocumentService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDocumentAccessService implements UserFileAccessService<UserDocument, RemovedDocument> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private RemovedDocumentService removedDocumentService;

    private UserDocument document;
    private User user;

    private boolean isDocumentPublic() {
        return document.getDocumentAttribute() == DocumentAttribute.PUBLIC;
    }

    private boolean isFriend() {
        User owner = document.getOwner();
        return document.getDocumentAttribute() == DocumentAttribute.FOR_FRIENDS
                && userService.areFriends(owner.getId(), user);
    }

    private boolean isOwner() {
        return document != null
                && user != null
                && document.getOwner().equals(user);
    }

    private boolean isReader() {
        return document != null
                && user != null
                && (document.getReaders().contains(user) || isInReadersGroup());
    }

    private boolean isInReadersGroup() {
        if (document != null && user != null) {
            for (FriendsGroup group : document.getReadersGroups()) {
                if (group.getFriends().contains(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEditor() {
        return document != null
                && user != null
                && (document.getEditors().contains(user) || isInEditorsGroup());
    }

    private boolean isInEditorsGroup() {
        if (document != null && user != null) {
            for (FriendsGroup group : document.getEditorsGroups()) {
                if (group.getFriends().contains(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isOwner(UserDocument file, User user) {
        this.user = user;
        document = file;
        return isOwner();
    }

    @Override
    public boolean isOwner(Long fileId, User user) {
        this.user = user;
        document = userDocumentService.getById(fileId);
        return isOwner();
    }

    @Override
    public boolean isOwner(Collection<UserDocument> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDocument file : files) {
                if (!isOwner(file, user)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canRead(UserDocument file, User user) {
        this.user = user;
        document = file;
        return document.getDocumentStatus() == DocumentStatus.ACTUAL
                && (isOwner() || isReader() || isEditor() || isDocumentPublic() || isFriend());
    }

    @Override
    public boolean canRemove(UserDocument file, User user) {
        this.user = user;
        document = file;
        return isOwner() && file.getDocumentStatus() == DocumentStatus.ACTUAL;
    }

    @Override
    public boolean canEdit(UserDocument file, User user) {
        this.user = user;
        document = file;
        return isOwner() || isEditor();
    }

    @Override
    public boolean canRead(Long fileId, User user) {
        UserDocument document = userDocumentService.getById(fileId);
        return canRead(document, user);
    }

    @Override
    public boolean canRemove(Long fileId, User user) {
        UserDocument document = userDocumentService.getById(fileId);
        return canRemove(document, user);
    }

    @Override
    public boolean canEdit(Long fileId, User user) {
        UserDocument document = userDocumentService.getById(fileId);
        return canEdit(document, user);
    }

    @Override
    public boolean canRead(List<UserDocument> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDocument file : files) {
                if (!canRead(file, user)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canRemove(Set<UserDocument> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDocument file : files) {
                if (!canRemove(file, user)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canEdit(List<UserDocument> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDocument file : files) {
                if (!canEdit(file, user)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canRecover(RemovedDocument removedFile, User user) {
        return removedFile != null
                && user != null
                && removedFile.getOwner().equals(user);
    }

    @Override
    public boolean canRecover(Long removedFileId, User user) {
        RemovedDocument removedDocument = removedDocumentService.getById(removedFileId);
        return canRecover(removedDocument, user);
    }
}
