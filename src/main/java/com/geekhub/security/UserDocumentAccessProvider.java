package com.geekhub.security;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import com.geekhub.service.UserDocumentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDocumentAccessProvider implements UserFileAccessProvider<UserDocument> {

    @Autowired
    private UserDocumentService userDocumentService;

    private UserDocument document;
    private User user;

    private boolean isOwner() {
        return document.getOwner().equals(user);
    }

    private boolean isReader() {
        return document.getReaders().contains(user) || isInReadersGroup();
    }

    private boolean isInReadersGroup() {
        for (FriendsGroup group : document.getReadersGroups()) {
            if (group.getFriends().contains(user)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEditor() {
        return document.getEditors().contains(user) || isInEditorsGroup();
    }

    private boolean isInEditorsGroup() {
        for (FriendsGroup group : document.getEditorsGroups()) {
            if (group.getFriends().contains(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canRead(UserDocument file, User user) {
        this.document = file;
        this.user = user;
        return isOwner() || isReader() || isEditor();
    }

    @Override
    public boolean canRemove(UserDocument file, User user) {
        this.document = file;
        this.user = user;
        return isOwner();
    }

    @Override
    public boolean canEdit(UserDocument file, User user) {
        this.document = file;
        this.user = user;
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
        for (UserDocument file : files) {
            if (!canRead(file, user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canRemove(List<UserDocument> files, User user) {
        for (UserDocument file : files) {
            if (!canRemove(file, user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canEdit(List<UserDocument> files, User user) {
        for (UserDocument file : files) {
            if (!canEdit(file, user)) {
                return false;
            }
        }
        return true;
    }
}
