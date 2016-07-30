package com.geekhub.utils;

import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.UserDocument;

public class DocumentVersionUtil {

    public static DocumentOldVersion createOldVersion(UserDocument document) {
        DocumentOldVersion oldVersion = new DocumentOldVersion();
        oldVersion.setName(document.getName());
        oldVersion.setSize(document.getSize());
        oldVersion.setHashName(document.getHashName());
        oldVersion.setModifierName(document.getModifierName());
        oldVersion.setModifierId(document.getModifierId());
        oldVersion.setLastModifyTime(document.getLastModifyTime());
        return oldVersion;
    }

    public static UserDocument recoverOldVersion(DocumentOldVersion oldVersion) {
        UserDocument document = oldVersion.getUserDocument();
        document.setHashName(oldVersion.getHashName());
        document.setLastModifyTime(oldVersion.getLastModifyTime());
        document.setModifierName(oldVersion.getModifierName());
        document.setModifierId(oldVersion.getModifierId());
        document.setSize(oldVersion.getSize());
        return document;
    }
}
