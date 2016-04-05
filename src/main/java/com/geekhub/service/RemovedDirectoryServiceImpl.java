package com.geekhub.service;

import com.geekhub.dao.RemovedDirectoryDao;
import com.geekhub.entity.RemovedDirectory;
import com.geekhub.entity.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RemovedDirectoryServiceImpl implements RemovedDirectoryService {

    @Autowired
    private RemovedDirectoryDao removedDirectoryDao;

    @Autowired
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
    public List<RemovedDirectory> getAllByOwnerId(Long ownerId) {
        User owner = userService.getById(ownerId);
        return removedDirectoryDao.getList("owner", owner);
    }
}
