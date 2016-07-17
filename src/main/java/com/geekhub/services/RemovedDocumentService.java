package com.geekhub.services;

import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface RemovedDocumentService extends EntityService<RemovedDocument, Long> {

    Set<RemovedDocument> getAllByOwnerId(Long ownerId);

    RemovedDocument getByOwnerAndDocument(User owner, UserDocument document);

    RemovedDocument getByUserDocumentId(Long documentId);
}
