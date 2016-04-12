package com.geekhub.services;

import com.geekhub.dao.RemovedDocumentDao;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RemovedDocumentServiceImpl implements RemovedDocumentService {

    @Autowired
    private RemovedDocumentDao removedDocumentDao;

    @Autowired
    private UserService userService;

    @Override
    public List<RemovedDocument> getAll(String orderParameter) {
        return removedDocumentDao.getAll(orderParameter);
    }

    @Override
    public RemovedDocument getById(Long id) {
        return removedDocumentDao.getById(id);
    }

    @Override
    public RemovedDocument get(String propertyName, Object value) {
        return removedDocumentDao.get(propertyName, value);
    }

    @Override
    public Long save(RemovedDocument entity) {
        return removedDocumentDao.save(entity);
    }

    @Override
    public void update(RemovedDocument entity) {
        removedDocumentDao.update(entity);
    }

    @Override
    public void delete(RemovedDocument entity) {
        removedDocumentDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        removedDocumentDao.deleteById(entityId);
    }

    @Override
    public Set<RemovedDocument> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return new HashSet<>(removedDocumentDao.getList("owner", owner));
    }

    @Override
    public RemovedDocument getByUserDocument(UserDocument document) {
        return removedDocumentDao.get(document.getOwner(), "userDocument", document);
    }

    @Override
    public RemovedDocument getByUserDocumentHashName(String userDocumentHashName) {
        return removedDocumentDao.getByUserDocumentHashName(userDocumentHashName);
    }
}