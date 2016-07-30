package com.geekhub.interceptors;

import com.geekhub.entities.FileSharedLink;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.interceptors.utils.InterceptorUtil;
import com.geekhub.interceptors.utils.RequestURL;
import com.geekhub.security.AccessPredicates;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.FileSharedLinkService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
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

    @PostConstruct
    public void init() {
        addRequestWithJSON(RequestURL.post("/api/links"));
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        RequestURL requestURL = new RequestURL(req.getRequestURI(), req.getMethod());
        if (isRequestWithJSON(requestURL)) {
            String requestBody = InterceptorUtil.getRequestBody(req);
            if (userId != null && requestBody.length() > 0) {
                JSONObject requestObject = new JSONObject(requestBody);
                Long fileId = requestObject.getLong("fileId");
                String fileType = requestObject.getString("fileType");
                if (fileType != null && permitAccess(fileId, FileType.valueOf(fileType), userId)) {
                    return true;
                }
            }
        }

        Map<String, String> pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (userId != null && !CollectionUtils.isEmpty(pathVariables)) {
            String fileId = pathVariables.get("fileId");
            String fileHash = pathVariables.get("fileHash");
            if ((fileHash != null && permitAccess(fileHash, userId))
                    || (StringUtils.isNumeric(fileId) && isFileAvailable(userId, Long.valueOf(fileId), req))) {
                return true;
            }
        }
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return false;
    }

    private boolean isFileAvailable(Long userId, Long fileId, HttpServletRequest request) {
        if (fileId == null) {
            return false;
        }
        FileType fileType = FileType.valueOf(request.getParameter("fileType"));
        return permitAccess(fileId, fileType, userId);
    }

    @Override
    public boolean permitAccess(Long objectId, Long userId, RequestURL url) {
        return false;
    }

    private boolean permitAccess(Long fileId, FileType fileType, Long userId) {
        User user = userService.getById(userId);
        if (fileType == FileType.DOCUMENT) {
            UserDocument document = userDocumentService.getById(fileId);
            return fileAccessService.permitAccess(document, user, AccessPredicates.DOCUMENT_OWNER);
        }
        UserDirectory directory = userDirectoryService.getById(fileId);
        return fileAccessService.permitAccess(directory, user, AccessPredicates.DIRECTORY_OWNER);
    }

    private boolean permitAccess(String fileHash, Long userId) {
        FileSharedLink sharedLink = fileSharedLinkService.getByFileHashName(fileHash);
        return Objects.equals(sharedLink.getUserId(), userId);
    }
}
