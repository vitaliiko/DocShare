package com.geekhub.service;

import com.geekhub.entity.RemovedDocument;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface RemovedDocumentService extends EntityService<RemovedDocument, Long> {

    List<RemovedDocument> getAllByOwnerId(Long ownerId);

    RemovedDocument getByUserDocument(UserDocument document);

    RemovedDocument getByUserDocumentHashName(String userDocumentHashName);

    RemovedDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);
}
