package com.geekhub.security;

import com.geekhub.entity.User;
import java.util.List;

public interface UserFileAccessProvider<T> {

    boolean canRead(T file, User user);

    boolean canRead(List<T> files, User user);

    boolean canRead(Long fileId, User user);

    boolean canRemove(T file, User user);

    boolean canRemove(List<T> files, User user);

    boolean canRemove(Long fileId, User user);

    boolean canEdit(T file, User user);

    boolean canEdit(List<T> files, User user);

    boolean canEdit(Long fileId, User user);

}
