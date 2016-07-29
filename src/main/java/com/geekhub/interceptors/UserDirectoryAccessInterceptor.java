package com.geekhub.interceptors;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.interceptors.utils.InterceptorUtil;
import com.geekhub.interceptors.utils.RequestURL;
import com.geekhub.security.AccessPredicates;
import com.geekhub.security.FileAccessService;
import com.geekhub.services.UserDirectoryService;
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
public class UserDirectoryAccessInterceptor extends AccessInterceptor<UserDirectory> {

    @Inject
    private UserService userService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private FileAccessService fileAccessService;

    @PostConstruct
    public void init() {
        addPredicate(RequestURL.post("/api/directories/*/documents/upload"), AccessPredicates.DIRECTORY_OWNER);
        addPredicate(RequestURL.post("/api/directories/*/make-dir"), AccessPredicates.DIRECTORY_OWNER);
        addPredicate(RequestURL.get("/api/directories/*/content"), AccessPredicates.DIRECTORY_READER);
        addPredicate(RequestURL.post("/api/directories/*/recover"), AccessPredicates.DIRECTORY_OWNER);
        addPredicate(RequestURL.post("/api/directories/*/rename"), AccessPredicates.DIRECTORY_OWNER);
        addPredicate(RequestURL.post("/api/directories/*/share"), AccessPredicates.DIRECTORY_OWNER);
        addPredicate(RequestURL.post("/api/directories/*/add-to-my-files"), AccessPredicates.NOT_DIRECTORY_OWNER);
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Map<String, String> pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (userId != null && !CollectionUtils.isEmpty(pathVariables)) {
            String dirId = pathVariables.get("dirId");
            String dirHashName = pathVariables.get("dirHashName");
            RequestURL url = InterceptorUtil.createRequestURL(req, pathVariables);
            if (isDirectoryAvailable(userId, dirHashName, url)
                    || (StringUtils.isNumeric(dirId) && isDirectoryAvailable(userId, Long.valueOf(dirId), url))) {
                return true;
            }
        }
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return false;
    }

    private boolean isDirectoryAvailable(Long userId, String dirHashName, RequestURL url) {
        return dirHashName != null && permitAccess(dirHashName, userId, url);
    }

    private boolean isDirectoryAvailable(Long userId, Long dirId, RequestURL url) {
        return dirId != null &&  permitAccess(dirId, userId, url);
    }

    @Override
    public boolean permitAccess(Long dirId, Long userId, RequestURL url) {
        BiPredicate<User, UserDirectory> predicate = getPredicate(url);
        if (predicate == null) {
            return true;
        }
        User user = userService.getById(userId);
        UserDirectory directory = userDirectoryService.getById(dirId);
        return fileAccessService.permitAccess(directory, user, predicate);
    }

    private boolean permitAccess(String dirHashName, Long userId, RequestURL url) {
        BiPredicate<User, UserDirectory> predicate = getPredicate(url);
        if (predicate == null) {
            return true;
        }
        User user = userService.getById(userId);
        UserDirectory directory = userDirectoryService.getByHashName(dirHashName);
        return dirHashName.equals("root")
                || dirHashName.equals(user.getLogin())
                || fileAccessService.permitAccess(directory, user, predicate);
    }
}
