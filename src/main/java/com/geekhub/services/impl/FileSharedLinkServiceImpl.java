package com.geekhub.services.impl;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.dto.convertors.DtoToEntityConverter;
import com.geekhub.entities.FileSharedLink;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.repositories.FileSharedLinkRepository;
import com.geekhub.services.FileSharedLinkService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.DateTimeUtils;
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
        FileSharedLink existingLink;
        existingLink = getByFileHashName(linkDto.getFileHashName());
        if (existingLink == null) {
            return create(linkDto, userId);
        }
        return update(linkDto, existingLink);

    }

    private FileSharedLink update(FileSharedLinkDto linkDto, FileSharedLink existingLink) {
        existingLink.setLastDate(DateTimeUtils.convertDate(linkDto.getLastDate()));
        if (existingLink.getMaxClickNumber() == 0 && linkDto.getMaxClickNumber() > 0) {
            existingLink.setClickNumber(0);
        }
        existingLink.setMaxClickNumber(linkDto.getMaxClickNumber());
        existingLink.setRelationType(linkDto.getRelationType() == null ? FileRelationType.READ : linkDto.getRelationType());
        update(existingLink);
        return existingLink;
    }

    private FileSharedLink create(FileSharedLinkDto linkDto, Long userId) {
        FileSharedLink newLink;
        newLink = DtoToEntityConverter.convert(linkDto);
        String linkHash = FileSharedLinkUtil.generateLinkHash(linkDto.getFileHashName(), linkDto.getFileType(), userId);
        newLink.setHash(linkHash);
        if (linkDto.getFileType() == FileType.DOCUMENT) {
            UserDocument document = userDocumentService.getByHashName(linkDto.getFileHashName());
            newLink.setFileHashName(document.getHashName());
        } else {
            UserDirectory directory = userDirectoryService.getByHashName(linkDto.getFileHashName());
            newLink.setFileHashName(directory.getHashName());
        }
        newLink.setUserId(userId);
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

    @Override
    public void deleteByFileHashName(String fileHash) {
        FileSharedLink fileSharedLink = getByFileHashName(fileHash);
        delete(fileSharedLink);
    }
}
