package com.geekhub.services.impl;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.dto.convertors.DtoToEntityConverter;
import com.geekhub.entities.FileSharedLink;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.repositories.FileSharedLinkRepository;
import com.geekhub.services.FileSharedLinkService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.enams.FileType;
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

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

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
    public FileSharedLink createOrUpdate(FileSharedLinkDto linkDto, Long userId) {
        String linkHash = FileSharedLinkUtil.generateLinkHash(linkDto.getFileId(), linkDto.getFileType(), userId);
        FileSharedLink existingLink = getByLinkHash(linkHash);
        if (existingLink == null) {
            return create(linkDto, linkHash);
        }
        return update(linkDto, existingLink);
    }

    private FileSharedLink update(FileSharedLinkDto linkDto, FileSharedLink existingLink) {
        existingLink.setLastDate(linkDto.getLastDate());
        if (existingLink.getMaxClickNumber() == 0 && linkDto.getMaxClickNumber() > 0) {
            existingLink.setClickNumber(0);
        }
        existingLink.setMaxClickNumber(linkDto.getMaxClickNumber());
        existingLink.setRelationType(linkDto.getRelationType());
        update(existingLink);
        return existingLink;
    }

    private FileSharedLink create(FileSharedLinkDto linkDto, String linkHash) {
        FileSharedLink newLink;
        newLink = DtoToEntityConverter.convert(linkDto);
        newLink.setHash(linkHash);
        if (linkDto.getFileType() == FileType.DOCUMENT) {
            UserDocument document = userDocumentService.getById(linkDto.getFileId());
            newLink.setFileHashName(document.getHashName());
        } else {
            UserDirectory directory = userDirectoryService.getById(linkDto.getFileId());
            newLink.setFileHashName(directory.getHashName());
        }
        save(newLink);
        return newLink;
    }

    @Override
    public FileSharedLink getByFileHashName(String fileHashName) {
        return repository.get("fileHashName", fileHashName);
    }

    @Override
    public FileSharedLink getByLinkHash(String linkHash) {
        return repository.get("hash", linkHash);
    }
}
