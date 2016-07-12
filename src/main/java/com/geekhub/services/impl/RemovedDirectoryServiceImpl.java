package com.geekhub.services.impl;

import com.geekhub.repositories.RemovedDirectoryRepository;
import com.geekhub.entities.RemovedDirectory;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.geekhub.services.RemovedDirectoryService;
import com.geekhub.services.UserService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RemovedDirectoryServiceImpl implements RemovedDirectoryService {

    @Inject
    private RemovedDirectoryRepository removedDirectoryDao;

    @Inject
    private UserService userService;

    @Override
    public List<RemovedDirectory> getAll(String orderParameter) {
        return removedDirectoryDao.getAll(orderParameter);
    }

    @Override
    public RemovedDirectory getById(Long id) {
        return removedDirectoryDao.getById(id);
    }

    @Override
    public RemovedDirectory get(String propertyName, Object value) {
        return removedDirectoryDao.get(propertyName, value);
    }

    @Override
    public Long save(RemovedDirectory entity) {
        return removedDirectoryDao.save(entity);
    }

    @Override
    public void update(RemovedDirectory entity) {
        removedDirectoryDao.update(entity);
    }

    @Override
    public void delete(RemovedDirectory entity) {
        removedDirectoryDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        removedDirectoryDao.deleteById(entityId);
    }

    @Override
    public Set<RemovedDirectory> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return new HashSet<>(removedDirectoryDao.getList("owner", owner));
    }

    @Override
    public RemovedDirectory getByUserDirectory(UserDirectory directory) {
        return removedDirectoryDao.get("userDirectory", directory);
    }
}
