package com.geekhub.interceptors;

import com.geekhub.entities.*;
import com.geekhub.interceptors.utils.InterceptorUtil;
import com.geekhub.interceptors.utils.RequestURL;
import com.geekhub.security.AccessPredicates;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class FileSharedLinkInterceptor extends AccessInterceptor<FileSharedLink> {

    @Inject
    private UserService userService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private FileAccessService fileAccessService;

    @Inject
    private FileSharedLinkService fileSharedLinkService;

    @Inject
    private FileSharedLinkTokenService fileSharedLinkTokenService;

    @PostConstruct
    public void init() {
        addRequestWithJSON(RequestURL.post("/api/links"));
        addRequestWithJSON(RequestURL.post("/api/links/documents/comments"));
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        try {
            Long userId = (Long) req.getSession().getAttribute("userId");
            RequestURL requestURL = new RequestURL(req.getRequestURI(), req.getMethod());
            if (isRequestWithJSON(requestURL) && preHandleWithJSON(req, userId)) {
                return true;
            }

            if (preHandle(req, userId)) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return false;
    }

    private boolean preHandle(HttpServletRequest req, Long userId) {
        Map<String, String> pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (CollectionUtils.isEmpty(pathVariables)) {
            String token = req.getParameter("token");
            return permitAccessByToken(token);
        }
        String fileId = pathVariables.get("fileId");
        String fileHash = pathVariables.get("fileHash");
        String linkHash = pathVariables.get("linkHash");
        return (permitAccessByFileHash(fileHash, userId))
                || (StringUtils.isNumeric(fileId) && isFileAvailable(userId, Long.valueOf(fileId), req))
                || permitAccessByLinkHash(linkHash);
    }

    private boolean preHandleWithJSON(HttpServletRequest req, Long userId) {
        String requestBody = InterceptorUtil.getRequestBody(req);
        if (requestBody.length() > 0) {
            JSONObject requestObject = new JSONObject(requestBody);
            String fileHashName = requestObject.getString("fileHashName");
            String fileType = requestObject.getString("fileType");
            String token = requestObject.getString("token");
            if (permitAccessByFileHashName(fileHashName, FileType.valueOf(fileType), userId)
                    || permitAccessByToken(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFileAvailable(Long userId, Long fileId, HttpServletRequest request) {
        if (fileId == null) {
            return false;
        }
        FileType fileType = FileType.valueOf(request.getParameter("fileType"));
        return permitAccessByFileId(fileId, fileType, userId);
    }

    @Override
    public boolean permitAccess(Long objectId, Long userId, RequestURL url) {
        return false;
    }

    private boolean permitAccessByFileHashName(String fileHashName, FileType fileType, Long userId) {
        if (fileHashName == null || userId == null) {
            return false;
        }
        User user = userService.getById(userId);
        if (fileType == FileType.DOCUMENT) {
            UserDocument document = userDocumentService.getByHashName(fileHashName);
            return fileAccessService.permitAccess(document, user, AccessPredicates.DOCUMENT_OWNER);
        }
        UserDirectory directory = userDirectoryService.getByHashName(fileHashName);
        return fileAccessService.permitAccess(directory, user, AccessPredicates.DIRECTORY_OWNER);
    }

    private boolean permitAccessByFileId(Long fileId, FileType fileType, Long userId) {
        if (fileId == null || userId == null) {
            return false;
        }
        User user = userService.getById(userId);
        if (fileType == FileType.DOCUMENT) {
            UserDocument document = userDocumentService.getById(fileId);
            return fileAccessService.permitAccess(document, user, AccessPredicates.DOCUMENT_OWNER);
        }
        UserDirectory directory = userDirectoryService.getById(fileId);
        return fileAccessService.permitAccess(directory, user, AccessPredicates.DIRECTORY_OWNER);
    }

    private boolean permitAccessByFileHash(String fileHash, Long userId) {
        if (fileHash == null || userId == null) {
            return false;
        }
        FileSharedLink sharedLink = fileSharedLinkService.getByFileHashName(fileHash);
        return Objects.equals(sharedLink.getUserId(), userId);
    }

    private boolean permitAccessByLinkHash(String linkHash) {
        if (linkHash == null) {
            return false;
        }
        FileSharedLink sharedLink = fileSharedLinkService.getByLinkHash(linkHash);
        return sharedLink != null;
    }

    private boolean permitAccessByToken(String token) {
        if (token == null) {
            return false;
        }
        FileSharedLinkToken sharedLinkToken = fileSharedLinkTokenService.getByToken(token);
        return sharedLinkToken != null;
    }
}
