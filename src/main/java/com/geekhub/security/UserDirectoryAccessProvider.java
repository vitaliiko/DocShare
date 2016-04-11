package com.geekhub.security;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDirectory;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.services.RemovedDirectoryService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserService;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDirectoryAccessProvider implements UserFileAccessProvider<UserDirectory, RemovedDirectory> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDirectoryService userDirectoryService;

    @Autowired
    private RemovedDirectoryService removedDirectoryService;

    private UserDirectory directory;
    private User user;

    private boolean isDocumentPublic() {
        return directory.getDocumentAttribute() == DocumentAttribute.PUBLIC;
    }

    private boolean isFriend() {
        User owner = directory.getOwner();
        return directory.getDocumentAttribute() == DocumentAttribute.FOR_FRIENDS
                && userService.areFriends(owner.getId(), user);
    }

    private boolean isOwner() {
        return directory != null
                && user != null
                && directory.getOwner().equals(user);
    }

    private boolean isReader() {
        return directory != null
                && user != null
                && (directory.getReaders().contains(user) || isInReadersGroup());
    }

    private boolean isInReadersGroup() {
        if (directory != null && user != null) {
            for (FriendsGroup group : directory.getReadersGroups()) {
                if (group.getFriends().contains(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isOwner(UserDirectory file, User user) {
        this.user = user;
        directory = file;
        return isOwner();
    }

    @Override
    public boolean isOwner(Long fileId, User user) {
        this.user = user;
        directory = userDirectoryService.getById(fileId);
        return isOwner();
    }

    @Override
    public boolean isOwner(List<UserDirectory> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDirectory file : files) {
                if (!isOwner(file, user)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canRead(UserDirectory file, User user) {
        this.user = user;
        directory = file;
        return isOwner() || isReader() || isFriend() || isDocumentPublic();
    }

    @Override
    public boolean canEdit(UserDirectory file, User user) {
        this.user = user;
        directory = file;
        return isOwner();
    }

    @Override
    public boolean canRemove(UserDirectory file, User user) {
        this.user = user;
        directory = file;
        return isOwner();
    }

    @Override
    public boolean canRead(Long fileId, User user) {
        UserDirectory directory = userDirectoryService.getById(fileId);
        return canRead(directory, user);
    }

    @Override
    public boolean canRemove(Long fileId, User user) {
        UserDirectory directory = userDirectoryService.getById(fileId);
        return canRemove(directory, user);
    }

    @Override
    public boolean canEdit(Long fileId, User user) {
        UserDirectory directory = userDirectoryService.getById(fileId);
        return canEdit(directory, user);
    }

    @Override
    public boolean canRead(List<UserDirectory> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDirectory file : files) {
                if (!canRead(file, user)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canRemove(Set<UserDirectory> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDirectory file : files) {
                if (!canRemove(file, user)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canEdit(List<UserDirectory> files, User user) {
        if (files != null && files.size() > 0) {
            for (UserDirectory file : files) {
                if (!canEdit(file, user)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canRecover(RemovedDirectory removedFile, User user) {
        return removedFile != null
                && user != null
                && removedFile.getOwner().equals(user);
    }

    @Override
    public boolean canRecover(Long removedFileId, User user) {
        RemovedDirectory removedDirectory = removedDirectoryService.getById(removedFileId);
        return removedDirectory != null
                && user != null
                && removedDirectory.getOwner().equals(user);
    }
}
