package com.geekhub.util;

import com.geekhub.entity.RemovedDirectory;
import com.geekhub.entity.RemovedDocument;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.entity.UserDocument;
import com.geekhub.entity.enums.DocumentAttribute;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;

public class UserFileUtil {

    public static final String ROOT_LOCATION = "C:\\spring_docs\\";
    public static final String SYSTEM_EXTENSION = ".curva";

    public static UserDocument createUserDocument(MultipartFile multipartFile,
                                                  String parentDirectoryHash,
                                                  String description,
                                                  User user) throws IOException {

        UserDocument document = new UserDocument();
        document.setName(multipartFile.getOriginalFilename());
        document.setParentDirectoryHash(parentDirectoryHash);
        document.setDescription(description);
        document.setLastModifyTime(Calendar.getInstance().getTime());
        document.setType(multipartFile.getContentType());
        document.setSize(convertSize(multipartFile.getSize()));
        document.setOwner(user);
        document.setHashName(createHashName());
        document.setDocumentAttribute(DocumentAttribute.PRIVATE);
        return document;
    }

    public static UserDocument updateUserDocument(UserDocument document,
                                                  MultipartFile multipartFile,
                                                  String description) throws IOException {

        if (description != null && !description.isEmpty()) {
            document.setDescription(description);
        }
        String hashName = UserFileUtil.createHashName();
        document.setLastModifyTime(Calendar.getInstance().getTime());
        document.setSize(convertSize(multipartFile.getSize()));
        document.setHashName(hashName);
        multipartFile.transferTo(UserFileUtil.createFile(hashName));
        return document;
    }

    private static String convertSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = new String[] {"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static RemovedDocument wrapUserDocument(UserDocument document, Long removerId) {
        RemovedDocument removedDocument = new RemovedDocument();
        removedDocument.setOwner(document.getOwner());
        removedDocument.setRemoverId(removerId);
        removedDocument.setRemovalDate(Calendar.getInstance().getTime());
        removedDocument.setUserDocument(document);
        return removedDocument;
    }

    public static RemovedDirectory wrapUserDirectory(UserDirectory directory, Long removerId) {
        RemovedDirectory removedDirectory = new RemovedDirectory();
        removedDirectory.setOwner(directory.getOwner());
        removedDirectory.setRemoverId(removerId);
        removedDirectory.setRemovalDate(Calendar.getInstance().getTime());
        removedDirectory.setUserDirectory(directory);
        return removedDirectory;
    }

    public static String createHashName() {
        return DigestUtils.md5Hex("" + new Date().getTime());
    }

    public static String createHashName(long... parameters) {
        StringBuilder preparedHashName = new StringBuilder("");
        Arrays.stream(parameters).forEach(preparedHashName::append);
        return DigestUtils.md5Hex(preparedHashName.toString());
    }

    public static File createFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            return new File(ROOT_LOCATION + fileName + SYSTEM_EXTENSION);
        }
        return null;
    }

    public static UserDirectory createUserDirectory(User owner, String parentDirectoryHash, String dirName) {
        UserDirectory directory = new UserDirectory();
        directory.setOwner(owner);
        directory.setName(dirName);
        directory.setParentDirectoryHash(parentDirectoryHash);
        directory.setHashName(createHashName());
        directory.setDocumentAttribute(DocumentAttribute.PRIVATE);
        return directory;
    }

    public static Map<String, Object> createPropertiesMap(User owner, String parentDirectoryHash, String name) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("owner", owner);
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("name", name);
        return propertiesMap;
    }

    public static void removeUserFiles(User user) {

    }
}
