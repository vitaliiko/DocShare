package com.geekhub.services;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.services.enams.FileType;

import java.util.List;
import java.util.Set;

public interface EventSendingService {

    void sendUpdateEvent(UserDocument document, User user);

    <T, S extends EntityService<T, Long>> void sendRemoveEvent(S service, FileType fileType, String fileName,
                                                               long fileId, User user);

    <T, S extends EntityService<T, Long>> void sendRecoverEvent(S service, FileType fileType, String fileName,
                                                                long fileId, User user);

    void sendRenameEvent(Set<User> readers, FileType fileType, String fileOldName,
                         String fileName, long fileId, User user);

    void sendShareEvent(Set<User> readers, FileType fileType, String fileName, long fileId, User user);

    void sendProhibitAccessEvent(Set<User> readers, FileType fileType, String fileName, User user);

    void sendToFriendRequestEvent(User user, User friend);

    void sendAddToFriendEvent(User user, User friend);

    void sendDeleteFromFriendEvent(User user, User friend);

    void sendShareEvent(User user, FriendsGroup group, Set<User> membersSet, Set<User> newMembersSet);
}
