package com.geekhub.service;

import com.geekhub.entity.Comment;
import com.geekhub.entity.UserDocument;
import java.util.Set;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserDocumentService extends EntityService<UserDocument, Long> {

    List<UserDocument> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long docId, Long removerId);

    void moveToTrash(Long[] docIds, Long removerId);

    void recover(Long docId);

    void recover(Long[] docIds);

    UserDocument getByNameAndOwnerId(Long ownerId, String name);

    UserDocument getByFullNameAndOwnerId(Long ownerId, String location, String name);

    UserDocument getDocumentWithComments(Long docId);
}
