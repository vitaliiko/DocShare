package com.geekhub.services;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.entities.FileSharedLink;

public interface FileSharedLinkService extends EntityService<FileSharedLink, Long> {

    FileSharedLink create(FileSharedLinkDto linkDto, Long userId);
}
