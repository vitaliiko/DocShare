package com.geekhub.security;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.RemovedDocument;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import com.geekhub.service.RemovedDocumentService;
import com.geekhub.service.UserDocumentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDocumentAccessProvider implements UserFileAccessProvider<UserDocument, RemovedDocument> {

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private RemovedDocumentService removedDocumentService;

    private UserDocument document;
    private User user;

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
    public boolean isOwner(List<UserDocument> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDocument file : files) {
                if (!isOwner(file, user)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canRead(UserDocument file, User user) {
        this.user = user;
        document = file;
        return isOwner() || isReader() || isEditor();
    }

    @Override
    public boolean canRemove(UserDocument file, User user) {
        this.user = user;
        document = file;
        return isOwner();
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
        }
        return true;
    }

    @Override
    public boolean canRemove(List<UserDocument> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDocument file : files) {
                if (!canRemove(file, user)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canEdit(List<UserDocument> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDocument file : files) {
                if (!canEdit(file, user)) {
                    return false;
                }
            }
        }
        return true;
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
        return removedDocument != null
                && user != null
                && removedDocument.getOwner().equals(user);
    }
}
