package com.geekhub.repositories;

import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;

public interface RelationRepository {

    void deleteByDocument(UserDocument document);

    void deleteByDirectory(UserDirectory directory);
}
