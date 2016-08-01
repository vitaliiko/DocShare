package com.geekhub.services.impl;

import com.geekhub.entities.FileSharedLinkToken;
import com.geekhub.repositories.FileSharedLinkTokenRepository;
import com.geekhub.services.FileSharedLinkTokenService;

import javax.inject.Inject;
import java.util.List;

public class FileSharedLinkTokenServiceImpl implements FileSharedLinkTokenService {

    @Inject
    private FileSharedLinkTokenRepository repository;
    
    @Override
    public List<FileSharedLinkToken> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public FileSharedLinkToken getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public FileSharedLinkToken get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(FileSharedLinkToken entity) {
        return repository.save(entity);
    }

    @Override
    public void update(FileSharedLinkToken entity) {
        repository.update(entity);
    }

    @Override
    public void delete(FileSharedLinkToken entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }
}
