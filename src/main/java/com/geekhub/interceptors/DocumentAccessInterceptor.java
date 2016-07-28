package com.geekhub.interceptors;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.interceptors.utils.InterceptorUtil;
import com.geekhub.interceptors.utils.RequestURL;
import com.geekhub.security.AccessPredicates;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import org.apache.commons.lang3.StringUtils;
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
        addPredicate(RequestURL.get("/api/documents/*"), AccessPredicates.DOCUMENT_READER);
        addPredicate(RequestURL.post("/api/documents/*/share"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.get("/api/documents/*/access"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.post("/api/documents/*/rename"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.get("/api/documents/*/history"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.get("/api/documents/*/download"), AccessPredicates.DOCUMENT_READER);
        addPredicate(RequestURL.post("/api/documents/*/recover"), AccessPredicates.REMOVED_DOCUMENT_OWNER);
        addPredicate(RequestURL.post("/api/documents/*/comment-ability"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.post("/api/documents/*/versions/*/recover"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.get("/api/documents/*/versions/*/download"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.get("/api/documents/*/comment"), AccessPredicates.DOCUMENT_COMMENTER);
        addPredicate(RequestURL.post("/api/documents/*/comment"), AccessPredicates.DOCUMENT_COMMENTER);
        addPredicate(RequestURL.delete("/api/documents/*/comment"), AccessPredicates.DOCUMENT_OWNER);
        addPredicate(RequestURL.post("/api/documents/*/add-to-my-files"), AccessPredicates.DOCUMENT_OWNER);
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Map<String, String> pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (userId != null && !CollectionUtils.isEmpty(pathVariables)) {
            String docId = pathVariables.get("docId");
            RequestURL url = InterceptorUtil.createRequestURL(req, pathVariables);
            if (docId != null && StringUtils.isNumeric(docId) && permitAccess(Long.valueOf(docId), userId, url)) {
                return true;
            }
        }
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return false;
    }

    @Override
    public boolean permitAccess(Long docId, Long userId, RequestURL url) {
        BiPredicate<User, UserDocument> predicate = getPredicate(url);
        if (predicate == null) {
            return true;
        }
        User user = userService.getById(userId);
        UserDocument document = userDocumentService.getById(docId);
        return fileAccessService.permitAccess(document, user, predicate);
    }
}
