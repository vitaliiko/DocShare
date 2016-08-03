package com.geekhub.services.impl;

import com.geekhub.entities.FileSharedLink;
import com.geekhub.entities.FileSharedLinkToken;
import com.geekhub.repositories.FileSharedLinkTokenRepository;
import com.geekhub.services.FileSharedLinkTokenService;
import com.geekhub.utils.FileSharedLinkUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@Service
@Transactional
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

    @Override
    public FileSharedLinkToken create(FileSharedLink sharedLink) {
        FileSharedLinkToken linkToken = new FileSharedLinkToken();
        String token = FileSharedLinkUtil.generateToken(sharedLink.getHash());
        linkToken.setToken(token);
        linkToken.setCreationDate(LocalDateTime.now());
        linkToken.setFileSharedLink(sharedLink);
        save(linkToken);
        return linkToken;
    }

    @Override
    public FileSharedLink getSharedLinkByToken(String token) {
        FileSharedLinkToken sharedLinkToken = repository.get("token", token);
        return sharedLinkToken.getFileSharedLink();
    }

    @Override
    public FileSharedLinkToken getByToken(String token) {
        return repository.get("token", token);
    }
}
