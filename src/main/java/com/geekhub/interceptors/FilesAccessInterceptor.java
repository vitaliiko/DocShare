package com.geekhub.interceptors;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.security.AccessPredicates;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.services.enams.FileType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilesAccessInterceptor extends AccessInterceptor {

    @Inject
    private UserService userService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private FileAccessService fileAccessService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (userId != null && !CollectionUtils.isEmpty(parameterMap)) {
            String[] dirIds = parameterMap.get("dirIds");
            String[] docIds = parameterMap.get("docIds");
            if ((dirIds != null && permitAccess(dirIds, FileType.DIRECTORY, userId))
                    || (docIds != null && permitAccess(docIds, FileType.DOCUMENT, userId))) {
                return true;
            }
        }
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return false;
    }

    @Override
    public boolean permitAccess(Long dirId, Long userId, String url) {
        return true;
    }

    private boolean permitAccess(String[] fileIds, FileType fileType, Long userId) {
        List<Long> idsInLong = Arrays.stream(fileIds).map(Long::valueOf).collect(Collectors.toList());
        User user = userService.getById(userId);
        if (fileType == FileType.DOCUMENT) {
            List<UserDocument> documents = new ArrayList<>(userDocumentService.getByIds(idsInLong));
            return fileAccessService.permitAccess(documents, user, AccessPredicates.DOCUMENTS_OWNER);
        }
        List<UserDirectory> directories = new ArrayList<>(userDirectoryService.getByIds(idsInLong));
        return fileAccessService.permitAccess(directories, user, AccessPredicates.DIRECTORIES_OWNER);
    }
}
