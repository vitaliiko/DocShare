package com.geekhub.interceptors;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class DocumentAccessInterceptor extends AccessInterceptor<UserDocument> {

    @Inject
    private FileAccessService fileAccessService;

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Map<String, String> pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (userId == null || CollectionUtils.isEmpty(pathVariables)) {
            return false;
        }
        User user = userService.getById(userId);
        UserDocument document = userDocumentService.getById(Long.valueOf(pathVariables.get("docId")));
        return permitAccess(document, user, req.getQueryString());
    }

    @Override
    public boolean permitAccess(UserDocument document, User user, String url) {
        return fileAccessService.permitAccess(document, user, getPredicate(url));
    }
}
