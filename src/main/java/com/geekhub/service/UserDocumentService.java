package com.geekhub.service;

import com.geekhub.entity.UserDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserDocumentService extends EntityService<UserDocument, Long> {

    List<UserDocument> getByOwnerId(Long ownerId);
}
