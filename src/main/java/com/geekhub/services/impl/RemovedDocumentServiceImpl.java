package com.geekhub.services.impl;

import com.geekhub.repositories.RemovedDocumentRepository;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.geekhub.services.RemovedDocumentService;
import com.geekhub.services.UserService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RemovedDocumentServiceImpl implements RemovedDocumentService {

    @Inject
    private RemovedDocumentRepository removedDocumentRepository;

    @Inject
    private UserService userService;

    @Override
    public List<RemovedDocument> getAll(String orderParameter) {
        return removedDocumentRepository.getAll(orderParameter);
    }

    @Override
    public RemovedDocument getById(Long id) {
        return removedDocumentRepository.getById(id);
    }

    @Override
    public RemovedDocument get(String propertyName, Object value) {
        return removedDocumentRepository.get(propertyName, value);
    }

    @Override
    public Long save(RemovedDocument entity) {
        return removedDocumentRepository.save(entity);
    }

    @Override
    public void update(RemovedDocument entity) {
        removedDocumentRepository.update(entity);
    }

    @Override
    public void delete(RemovedDocument entity) {
        removedDocumentRepository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        removedDocumentRepository.deleteById(entityId);
    }

    @Override
    public Set<RemovedDocument> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return new HashSet<>(removedDocumentRepository.getList("owner", owner));
    }

    @Override
    public RemovedDocument getByUserDocument(UserDocument document) {
        return removedDocumentRepository.get(document.getOwner(), "userDocument", document);
    }

    @Override
    public RemovedDocument getByUserDocumentHashName(String userDocumentHashName) {
        return removedDocumentRepository.getByUserDocumentHashName(userDocumentHashName);
    }
}
