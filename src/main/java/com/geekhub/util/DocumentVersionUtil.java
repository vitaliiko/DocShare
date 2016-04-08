package com.geekhub.util;

import com.geekhub.entity.DocumentOldVersion;
import com.geekhub.entity.UserDocument;
import java.io.IOException;

public class DocumentVersionUtil {

    public static DocumentOldVersion saveOldVersion(UserDocument document, String description) throws IOException {
        DocumentOldVersion oldVersion = new DocumentOldVersion();
        oldVersion.setChangedBy(description);
        oldVersion.setName(document.getName());
        oldVersion.setSize(document.getSize());
        oldVersion.setHashName(document.getHashName());
        oldVersion.setLastModifyDate(document.getLastModifyTime());
        return oldVersion;
    }
}
