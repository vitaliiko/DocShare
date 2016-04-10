package com.geekhub.security;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.service.UserDirectoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDirectoryAccessProvider implements UserFileAccessProvider<UserDirectory> {

    @Autowired
    private UserDirectoryService userDirectoryService;

    private UserDirectory directory;
    private User user;

    private boolean isOwner() {
        return directory.getOwner().equals(user);
    }

    private boolean isReader() {
        return directory.getReaders().contains(user) || isInReadersGroup();
    }

    private boolean isInReadersGroup() {
        for (FriendsGroup group : directory.getReadersGroups()) {
            if (group.getFriends().contains(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canRead(UserDirectory file, User user) {
        this.directory = file;
        this.user = user;
        return isOwner() || isReader();
    }

    @Override
    public boolean canEdit(UserDirectory file, User user) {
        this.directory = file;
        this.user = user;
        return isOwner();
    }

    @Override
    public boolean canRemove(UserDirectory file, User user) {
        this.directory = file;
        this.user = user;
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
        for (UserDirectory file : files) {
            if (!canRead(file, user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canRemove(List<UserDirectory> files, User user) {
        for (UserDirectory file : files) {
            if (!canRead(file, user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canEdit(List<UserDirectory> files, User user) {
        for (UserDirectory file : files) {
            if (!canRead(file, user)) {
                return false;
            }
        }
        return true;
    }
}
