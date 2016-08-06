package com.geekhub.interceptors;

import com.geekhub.interceptors.utils.RequestURL;
import org.json.JSONObject;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.interceptors.utils.InterceptorUtil;
import com.geekhub.security.AccessPredicates;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.services.enams.FileType;
import com.geekhub.utils.CollectionTools;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserFilesAccessInterceptor extends AccessInterceptor {

    @Inject
    private UserService userService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private FileAccessService fileAccessService;

    @PostConstruct
    public void init() {
        addRequestWithJSON(RequestURL.post("/api/files/copy"));
        addRequestWithJSON(RequestURL.post("/api/files/replace"));
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        RequestURL requestURL = new RequestURL(req.getRequestURI(), req.getMethod());
        if (isRequestWithJSON(requestURL) && preHandleWithJSON(req, userId)) {
            return true;
        }
        if (preHandle(req, userId)) {
            return true;
        }
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return false;
    }

    private boolean preHandleWithJSON(HttpServletRequest req, Long userId) {
        String requestBody = InterceptorUtil.getRequestBody(req);
        if (userId != null && requestBody.length() > 0) {
            JSONObject requestObject = new JSONObject(requestBody);
            List<String> dirIds = InterceptorUtil.getStringList(requestObject, "dirIds");
            List<String> docIds = InterceptorUtil.getStringList(requestObject, "docIds");
            String destinationDirHash = requestObject.getString("destinationDirHash");
            if (areFilesAvailable(dirIds, docIds, userId) && isDirectoryAvailable(destinationDirHash, userId)) {
                return true;
            }
        }
        return false;
    }

    private boolean preHandle(HttpServletRequest req, Long userId) {
        String dirIds = req.getParameter("dirIds[]");
        String docIds = req.getParameter("docIds[]");
        return areFilesAvailable(parseIdArray(dirIds), parseIdArray(docIds), userId);
    }

    private List<String> parseIdArray(String arrayInLine) {
        if (arrayInLine == null) {
            return new ArrayList<>();
        }
        String[] ids = arrayInLine.split(",");
        return Arrays.asList(ids);
    }

    private boolean areFilesAvailable(List<String> dirIds, List<String> docIds, Long userId) {
        return CollectionTools.isAllNumeric(dirIds, docIds)
                && (!CollectionUtils.isEmpty(dirIds) && permitAccess(dirIds, FileType.DIRECTORY, userId)
                || !CollectionUtils.isEmpty(docIds) && permitAccess(docIds, FileType.DOCUMENT, userId));
    }

    private boolean isDirectoryAvailable(String destinationDirHash, Long userId) {
        return destinationDirHash == null || permitAccess(destinationDirHash, userId);
    }

    @Override
    public boolean permitAccess(Long dirId, Long userId, RequestURL url) {
        return true;
    }

    private boolean permitAccess(List<String> fileIds, FileType fileType, Long userId) {
        List<Long> idsInLong = fileIds.stream().map(Long::valueOf).collect(Collectors.toList());
        User user = userService.getById(userId);
        if (fileType == FileType.DOCUMENT) {
            List<UserDocument> documents = new ArrayList<>(userDocumentService.getAllByIds(idsInLong));
            return fileAccessService.permitAccess(documents, user, AccessPredicates.DOCUMENTS_OWNER);
        }
        List<UserDirectory> directories = new ArrayList<>(userDirectoryService.getAllByIds(idsInLong));
        return fileAccessService.permitAccess(directories, user, AccessPredicates.DIRECTORIES_OWNER);
    }

    private boolean permitAccess(String dirHashName, Long userId) {
        User user = userService.getById(userId);
        UserDirectory directory = userDirectoryService.getByHashName(dirHashName);
        return dirHashName.equals("root")
                || dirHashName.equals(user.getLogin())
                || fileAccessService.permitAccess(directory, user, AccessPredicates.DIRECTORY_OWNER);
    }
}
