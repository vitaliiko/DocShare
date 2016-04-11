package com.geekhub.utils;

import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.UserDocument;
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
