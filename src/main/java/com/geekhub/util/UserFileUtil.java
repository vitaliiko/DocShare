package com.geekhub.util;

import com.geekhub.entity.RemovedDocument;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.entity.UserDocument;
import com.geekhub.entity.UserFile;
import com.geekhub.entity.enums.DocumentAttribute;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;

public class UserFileUtil {

    public static final String ROOT_LOCATION = "C:\\spring_docs\\";
    public static final String SYSTEM_EXTENSION = ".curva";
    public static final String ROOT_DIRECTORY_HASH = createHashName(0L, 0L);

    public static <T extends UserFile> Map<String, Set<T>> prepareUserFileListMap(List<T> allDocumentsList) {
        Set<T> allDocuments = new TreeSet<>(allDocumentsList);
        Set<T> privateDocuments = new TreeSet<>();
        Set<T> publicDocuments = new TreeSet<>();
        Set<T> forFriendsDocuments = new TreeSet<>();
        allDocuments.forEach(doc -> {
            if (doc.getDocumentAttribute() == DocumentAttribute.PRIVATE) {
                privateDocuments.add(doc);
            }
            if (doc.getDocumentAttribute() == DocumentAttribute.PUBLIC) {
                publicDocuments.add(doc);
            }
            if (doc.getDocumentAttribute() == DocumentAttribute.FOR_FRIENDS) {
                forFriendsDocuments.add(doc);
            }
        });
        Map<String, Set<T>> userDocumentsListMap = new HashMap<>();
        userDocumentsListMap.put("allDocumentsTable", allDocuments);
        userDocumentsListMap.put("privateDocumentsTable", privateDocuments);
        userDocumentsListMap.put("publicDocumentsTable", publicDocuments);
        userDocumentsListMap.put("forFriendsDocumentsTable", forFriendsDocuments);
        return userDocumentsListMap;
    }

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
        document.setSize(calculateSize(multipartFile.getSize()));
        document.setOwner(user);
        document.setDocumentAttribute(DocumentAttribute.PRIVATE);
        return document;
    }

    public static UserDocument updateUserDocument(UserDocument document,
                                                  MultipartFile multipartFile,
                                                  String description) throws IOException {

        if (description != null && !description.isEmpty()) {
            document.setDescription(description);
        }
        document.setLastModifyTime(Calendar.getInstance().getTime());
        document.setSize(calculateSize(multipartFile.getSize()));
        return document;
    }

    private static String calculateSize(long size) {
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

    public static String createHashName(long ownerId, long docId) {
        return DocumentNameDigest.hashName("" + ownerId + docId);
    }

    public static String createHashName(long... parameters) {
        StringBuilder preparedHashName = new StringBuilder("");
        Arrays.stream(parameters).forEach(preparedHashName::append);
        return DocumentNameDigest.hashName(preparedHashName.toString());
    }

    public static File createFile(String fileName, String rootUserDirectory) {
        return new File(getFullFileName(fileName, rootUserDirectory));
    }

    public static String getFullFileName(String fileName, String rootUserDirectory) {
        return ROOT_LOCATION + rootUserDirectory + "\\" + fileName + SYSTEM_EXTENSION;
    }

    public static UserDirectory createUserDirectory(User owner, String parentDirectoryHash, String dirName) {
        UserDirectory directory = new UserDirectory();
        directory.setOwner(owner);
        directory.setName(dirName);
        directory.setParentDirectoryHash(parentDirectoryHash);
        directory.setDocumentAttribute(DocumentAttribute.PRIVATE);
        return directory;
    }

    public static void createDirInFileSystem(UserDirectory directory) {
        File file = new File(ROOT_LOCATION + "\\" + directory.getHashName());
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static Map<String, Object> createPropertiesMap(User owner, String parentDirectoryHash, String name) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("owner", owner);
        propertiesMap.put("parentDirectoryHash", parentDirectoryHash);
        propertiesMap.put("name", name);
        return propertiesMap;
    }

    public static void removeUserFiles(String userDirectory) {
        try {
            FileUtils.deleteDirectory(new File(ROOT_LOCATION + userDirectory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
