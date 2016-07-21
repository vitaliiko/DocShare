package com.geekhub.interceptors;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.interceptors.utils.InterceptorUtil;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.BiPredicate;

@Service
public class DocumentAccessInterceptor extends AccessInterceptor<UserDocument> {

    @Inject
    private FileAccessService fileAccessService;

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @PostConstruct
    public void init() {
        addPredicate("/api/documents/*", FileAccessService.DOCUMENT_READER);
        addPredicate("/api/documents/*/share", FileAccessService.DOCUMENT_OWNER);
        addPredicate("/api/documents/*/access", FileAccessService.DOCUMENT_OWNER);
        addPredicate("/api/documents/*/rename", FileAccessService.DOCUMENT_OWNER);
        addPredicate("/api/documents/*/history", FileAccessService.DOCUMENT_OWNER);
        addPredicate("/api/documents/*/download", FileAccessService.DOCUMENT_READER);
        addPredicate("/api/documents/*/recover", FileAccessService.REMOVED_DOCUMENT_OWNER);
        addPredicate("/api/documents/*/comment-ability", FileAccessService.DOCUMENT_OWNER);
        addPredicate("/api/documents/*/versions/*/recover", FileAccessService.DOCUMENT_OWNER);
        addPredicate("/api/documents/*/versions/*/download", FileAccessService.DOCUMENT_OWNER);
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Map<String, String> pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (userId == null || CollectionUtils.isEmpty(pathVariables)) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return false;
        }
        if (pathVariables.get("docId") == null) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return false;
        }
        String url = InterceptorUtil.removeVariablesFromURI(req, pathVariables);
        String docId = pathVariables.get("docId");
        if (permitAccess(Long.valueOf(docId), userId, url)) {
            return true;
        }
        resp.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
    }

    @Override
    public boolean permitAccess(Long docId, Long userId, String url) {
        BiPredicate<User, UserDocument> predicate = getPredicate(url);
        User user = userService.getById(userId);
        UserDocument document = userDocumentService.getById(docId);
        return predicate == null || fileAccessService.permitAccess(document, user, predicate);
    }
}
