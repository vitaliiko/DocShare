package com.geekhub.utils;

import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.UserDocument;
import java.io.IOException;

public class DocumentVersionUtil {

    public static DocumentOldVersion createOldVersion(UserDocument document) throws IOException {
        DocumentOldVersion oldVersion = new DocumentOldVersion();
        oldVersion.setModifiedBy(document.getModifiedBy());
        oldVersion.setName(document.getName());
        oldVersion.setSize(document.getSize());
        oldVersion.setHashName(document.getHashName());
        oldVersion.setLastModifyTime(document.getLastModifyTime());
        return oldVersion;
    }
}
