package com.geekhub.services.impl;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.dto.convertors.DtoToEntityConverter;
import com.geekhub.entities.FileSharedLink;
import com.geekhub.repositories.FileSharedLinkRepository;
import com.geekhub.services.FileSharedLinkService;
import com.geekhub.utils.FileSharedLinkUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class FileSharedLinkServiceImpl implements FileSharedLinkService {
    
    @Inject
    private FileSharedLinkRepository repository;

    @Override
    public List<FileSharedLink> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public FileSharedLink getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public FileSharedLink get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(FileSharedLink entity) {
        return repository.save(entity);
    }

    @Override
    public void update(FileSharedLink entity) {
        repository.update(entity);
    }

    @Override
    public void delete(FileSharedLink entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public FileSharedLink create(FileSharedLinkDto linkDto, Long userId) {
        FileSharedLink link = DtoToEntityConverter.convert(linkDto);
        String linkHash = FileSharedLinkUtil.generateLinkHash(link.getFileId(), userId);
        link.setHash(linkHash);
        save(link);
        return link;
    }
}
