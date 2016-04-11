package com.geekhub.services;

import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface RemovedDocumentService extends EntityService<RemovedDocument, Long> {

    List<RemovedDocument> getAllByOwnerId(Long ownerId);

    RemovedDocument getByUserDocument(UserDocument document);

    RemovedDocument getByUserDocumentHashName(String userDocumentHashName);

    RemovedDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);
}
