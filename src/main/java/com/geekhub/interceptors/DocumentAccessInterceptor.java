package com.geekhub.interceptors;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.security.FileAccessService;
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

    @PostConstruct
    public void init() {
        addPredicate("/api/documents/*", FileAccessService.OWNER)
        .addPredicate("/api/documents/*/share", FileAccessService.OWNER)
        .addPredicate("/api/documents/*/rename", FileAccessService.OWNER)
        .addPredicate("/api/documents/*/history", FileAccessService.OWNER)
        .addPredicate("/api/documents/*/recover", FileAccessService.OWNER_OF_REMOVED)
        .addPredicate("/api/documents/*/download", FileAccessService.READER)
        .addPredicate("/api/documents/*/browse", FileAccessService.READER)
        .addPredicate("/api/documents/*/comment-ability", FileAccessService.OWNER)
        .addPredicate("/api/documents/*/versions/*/recover", FileAccessService.OWNER)
        .addPredicate("/api/documents/*/versions/*/download", FileAccessService.OWNER);
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Map<String, String> pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (userId == null || CollectionUtils.isEmpty(pathVariables)) {
            return false;
        }
        String docId = pathVariables.get("docId");
        if (docId == null) {
            return false;
        }
        String url = req.getRequestURI();
        for (String var : pathVariables.values()) {
            url = url.replace(var, "*");
        }
        if (permitAccess(Long.valueOf(docId), userId, url)) {
            return true;
        } else {
            resp.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
    }

    @Override
    public boolean permitAccess(Long docId, Long userId, String url) {
        BiPredicate<User, UserDocument> predicate = getPredicate(url);
        return predicate == null || fileAccessService.permitAccess(docId, userId, predicate);
    }
}
