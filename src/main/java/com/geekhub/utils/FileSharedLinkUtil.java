package com.geekhub.utils;

import com.geekhub.entities.FileSharedLink;
import com.geekhub.services.enams.FileType;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;

public class FileSharedLinkUtil {

    public static final String BASE_URL = "http://127.0.0.1:8888/api/link/";

    public static String generateLinkHash(String fileHashName, FileType fileType, Long userId) {
        return DigestUtils.sha256Hex(fileType.toString() + fileHashName + userId + LocalDateTime.now());
    }

    public static String generateToken(String linkHash) {
        return DigestUtils.sha256Hex(linkHash + LocalDateTime.now());
    }

    public static String generateURL(FileSharedLink fileSharedLink) {
        if (fileSharedLink.getFileType() == FileType.DOCUMENT) {
            return BASE_URL + fileSharedLink.getHash() + "/documents";
        }
        return BASE_URL + fileSharedLink.getHash() + "/directories";
    }
}
