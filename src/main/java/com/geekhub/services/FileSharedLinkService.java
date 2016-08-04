package com.geekhub.services;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.entities.FileSharedLink;

public interface FileSharedLinkService extends EntityService<FileSharedLink, Long> {

    FileSharedLink createOrUpdate(FileSharedLinkDto linkDto, Long userId);

    FileSharedLink getByFileHashName(String fileHashName);

    FileSharedLink getByLinkHash(String linkHash);

    void deleteByFileHashName(String fileHash);
}
