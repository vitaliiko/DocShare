package com.geekhub.services;

import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface RemovedDocumentService extends EntityService<RemovedDocument, Long> {

    Set<RemovedDocument> getAllByOwnerId(Long ownerId);

    RemovedDocument getByUserDocument(UserDocument document);

    RemovedDocument getByUserDocumentHashName(String userDocumentHashName);
}
