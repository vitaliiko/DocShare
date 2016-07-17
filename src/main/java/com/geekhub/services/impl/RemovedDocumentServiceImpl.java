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
    private RemovedDocumentRepository repository;

    @Inject
    private UserService userService;

    @Override
    public List<RemovedDocument> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public RemovedDocument getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public RemovedDocument get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(RemovedDocument entity) {
        return repository.save(entity);
    }

    @Override
    public void update(RemovedDocument entity) {
        repository.update(entity);
    }

    @Override
    public void delete(RemovedDocument entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public Set<RemovedDocument> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return new HashSet<>(repository.getList("owner", owner));
    }

    @Override
    public RemovedDocument getByOwnerAndDocument(User owner, UserDocument document) {
        return repository.get(owner, "userDocument", document);
    }

    @Override
    public RemovedDocument getByUserDocumentId(Long documentId) {
        return repository.getByDocumentId(documentId);
    }
}
