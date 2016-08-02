package com.geekhub.utils;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.services.enams.FileType;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.Calendar;

public class FileSharedLinkUtil {

    public static final String BASE_DOCUMENT_URL = "http://127.0.0.1:8888/api/documents/link/";
    public static final String BASE_DIRECTORY_URL = "http://127.0.0.1:8888/api/directories/link/";

    public static String generateLinkHash(Long fileId, FileType fileType, Long userId) {
        return DigestUtils.md5Hex(fileType.toString() + fileId + userId);
    }

    public static String createFileShareURL(Long fileId, FileType fileType, Long userId) {
        String linkHash = generateLinkHash(fileId, fileType, userId);
        if (fileType == FileType.DOCUMENT) {
            return BASE_DOCUMENT_URL + linkHash;
        }
        return BASE_DIRECTORY_URL + linkHash;
    }

    public static String createFileShareURL(FileSharedLinkDto linkDto, Long userId) {
        return createFileShareURL(linkDto.getFileId(), linkDto.getFileType(), userId);
    }

    public static String generateToken(String linkHash) {
        return DigestUtils.sha256Hex(linkHash + Calendar.getInstance().getTimeInMillis());
    }
}
