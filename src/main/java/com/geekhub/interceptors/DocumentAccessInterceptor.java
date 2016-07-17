package com.geekhub.interceptors;

import com.geekhub.entities.UserToDocumentRelation;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.UserToDocumentRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class DocumentAccessInterceptor extends AccessInterceptor<UserToDocumentRelation> {

    @Inject
    private FileAccessService fileAccessService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @PostConstruct
    public void init() {
        addPredicate("/api/documents/*/share", FileAccessService.SHARE);
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
        return permitAccess(Long.valueOf(docId), userId, url);
    }

    @Override
    public boolean permitAccess(Long docId, Long userId, String url) {
        UserToDocumentRelation relation = userToDocumentRelationService.getByDocumentIdAndUserId(docId, userId);
        Predicate<UserToDocumentRelation> predicate = getPredicate(url);
        return predicate == null || fileAccessService.permitAccess(relation, predicate);
    }
}
