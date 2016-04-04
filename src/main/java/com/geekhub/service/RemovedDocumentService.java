package com.geekhub.service;

import com.geekhub.entity.RemovedDocument;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface RemovedDocumentService extends EntityService<RemovedDocument, Long> {

    List<RemovedDocument> getAllByOwnerId(Long ownerId);
}
