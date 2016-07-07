package com.geekhub.controllers.utils;

import com.geekhub.services.EntityService;
import com.geekhub.utils.UserFileUtil;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileControllersUtil {

    public static void openOutputStream(String docHashName, String docType, String docName, HttpServletResponse response)
            throws IOException {

        File file = UserFileUtil.createFile(docHashName);
        response.setContentType(docType);
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\"");

        FileCopyUtils.copy(Files.newInputStream(file.toPath()), response.getOutputStream());
    }

    public static <T, S extends EntityService<T, Long>> Set<T> createEntitySet(List<Long> ids, S service) {
        return ids.stream()
                .map(service::getById)
                .collect(Collectors.toSet());
    }
}
