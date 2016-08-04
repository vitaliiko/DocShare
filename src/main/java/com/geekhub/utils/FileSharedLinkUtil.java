package com.geekhub.utils;

import com.geekhub.dto.FileSharedLinkDto;
import com.geekhub.entities.FileSharedLink;
import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.enams.FileType;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class FileSharedLinkUtil {

    public static final String BASE_URL = "http://127.0.0.1:8888/api/links/";

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

    public static void checkSharedLinkDto(FileSharedLinkDto linkDto) throws IOException {
        try {
            if (linkDto.getMaxClickNumber() < 0 || linkDto.getMaxClickNumber() > 1024) {
                throw new IllegalArgumentException("Max click number must be in range from 0 to 1024");
            }
            if (linkDto.getRelationType() == FileRelationType.OWN) {
                throw new IllegalArgumentException("Wrong relation type");
            }
            Instant dateInstant = linkDto.getLastDate().toInstant();
            Instant nowInstant = ZonedDateTime.now().toInstant();
            if (dateInstant.isAfter(nowInstant)) {
                throw new IllegalArgumentException("Wrong date");
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }
    }
}
