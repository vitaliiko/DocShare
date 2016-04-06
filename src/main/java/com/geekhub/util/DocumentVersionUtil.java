package com.geekhub.util;

import com.geekhub.entity.DocumentOldVersion;
import com.geekhub.entity.UserDocument;
import java.io.File;
import java.io.IOException;
import org.springframework.util.FileCopyUtils;

public class DocumentVersionUtil {

    public static DocumentOldVersion saveOldVersion(UserDocument document, String description) throws IOException {
        long version = document.getDocumentOldVersions().size() + 1;
        String hashName = UserFileUtil.createHashName(document.getOwner().getId(), document.getId(), version);

        DocumentOldVersion oldVersion = new DocumentOldVersion();
        oldVersion.setUserDocument(document);
        oldVersion.setVersion(version);
        oldVersion.setDescription(description);
        oldVersion.setHashName(hashName);

        File currentVersionFile =
                new File(UserFileUtil.getFullFileName(document.getHashName(), document.getOwner().getRootDirectory()));
        File oldVersionFile =
                new File(UserFileUtil.getFullFileName(hashName, document.getOwner().getRootDirectory()));
        FileCopyUtils.copy(currentVersionFile, oldVersionFile);

        return oldVersion;
    }
}
