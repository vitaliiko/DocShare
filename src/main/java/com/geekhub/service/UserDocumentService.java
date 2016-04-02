package com.geekhub.service;

import com.geekhub.entity.UserDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserDocumentService extends EntityService<UserDocument, Long> {

    List<UserDocument> getAllByOwnerId(Long ownerId);

    List<UserDocument> getActualByOwnerId(Long ownerId);

    List<UserDocument> getRemovedByOwnerId(Long ownerId);

    void moveToTrash(Long docId);

    void recover(Long docId);

    UserDocument getByNameAndOwnerId(Long ownerId, String name);
}
