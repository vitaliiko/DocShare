package com.geekhub.repositories.impl;

import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.repositories.RelationRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class RelationRepositoryImpl implements RelationRepository {

    @Inject
    private SessionFactory sessionFactory;

    @Override
    public void deleteByDocument(UserDocument document) {

    }

    @Override
    public void deleteByDirectory(UserDirectory directory) {

    }
}
