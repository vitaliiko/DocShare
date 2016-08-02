package com.geekhub.services;

import com.geekhub.entities.FileSharedLink;
import com.geekhub.entities.FileSharedLinkToken;

public interface FileSharedLinkTokenService extends EntityService<FileSharedLinkToken, Long> {

    FileSharedLinkToken create(FileSharedLink sharedLink);

    FileSharedLink getSharedLinkByToken(String token);

    FileSharedLinkToken getByToken(String token);
}
