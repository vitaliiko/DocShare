package com.geekhub.services;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.entities.FileSharedLink;

import java.io.IOException;

public interface FileSharedLinkService extends EntityService<FileSharedLink, Long> {

    FileSharedLink createOrUpdate(FileSharedLinkDto linkDto, Long userId) throws IOException;

    FileSharedLink getByFileHashName(String fileHashName);

    FileSharedLink getByLinkHash(String linkHash);
}
