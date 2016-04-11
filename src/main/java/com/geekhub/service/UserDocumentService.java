package com.geekhub.service;

import com.geekhub.entity.Comment;
import com.geekhub.entity.DocumentOldVersion;
import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.entity.UserDocument;
import java.util.Set;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserDocumentService extends EntityService<UserDocument, Long> {

    Set<UserDocument> getByIds(List<Long> docIds);

    List<UserDocument> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long docId, Long removerId);

    void moveToTrash(Long[] docIds, Long removerId);

    Long recover(Long removedDocId);

    void recover(Long[] removedDocIds);

    UserDocument getByNameAndOwnerId(Long ownerId, String name);

    UserDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);

    UserDocument getDocumentWithComments(Long docId);

    UserDocument getDocumentWithOldVersions(Long docId);

    Set<User> getAllReadersAndEditors(Long docId);

    Set<DocumentOldVersion> getOldVersions(Long docId);

    UserDocument getWithOldVersions(Long docId);

    List<UserDocument> getAllByParentDirectoryHash(String parentDirectoryHash);
}
