package com.geekhub.controllers;

import com.geekhub.dto.SharedDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.exceptions.ResourceNotFoundException;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserService;
import com.geekhub.utils.UserFileUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserDirectoryController {

    @Inject
    private UserService userService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private UserDirectoryAccessService directoryAccessService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping(value = "/directories/{removedDirId}/recover", method = RequestMethod.POST)
    public ModelAndView recoverDirectory(@PathVariable long removedDirId, HttpSession session) {
        User user = getUserFromSession(session);
        if (directoryAccessService.canRecover(removedDirId, user)) {
            userDirectoryService.recover(removedDirId);
            return new ModelAndView("redirect:/api/documents");
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/directories", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> makeDir(@RequestParam String dirName,
                                               @RequestParam(required = false, defaultValue = "root") String parentDirHash,
                                               HttpSession session) {

        User owner = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getByFullNameAndOwner(owner, parentDirHash, dirName);
        if (directory != null || !UserFileUtil.validateDirectoryName(dirName)) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDirectory newDirectory = userDirectoryService.createDirectory(owner, parentDirHash, dirName);
        return ResponseEntity.ok(EntityToDtoConverter.convert(newDirectory));
    }

    @RequestMapping(value = "/directories/{dirId}", method = RequestMethod.GET)
    public ResponseEntity<UserFileDto> getUserDirectory(@PathVariable Long dirId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(dirId);
        if (directoryAccessService.isOwnerOfActual(directory, user)) {
            return ResponseEntity.ok(EntityToDtoConverter.convert(directory));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @RequestMapping(value = "/directories/{dirId}/rename", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> renameDirectory(@PathVariable Long dirId,
                                                       @RequestParam String newDirName,
                                                       HttpSession session) {

        UserDirectory directory = userDirectoryService.getById(dirId);
        User user = getUserFromSession(session);

        if (directoryAccessService.isOwnerOfActual(directory, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        if (directory.getName().equals(newDirName)) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDirectory existingDirectory =
                userDirectoryService.getByFullNameAndOwner(user, directory.getParentDirectoryHash(), newDirName);
        if (existingDirectory == null && UserFileUtil.validateDirectoryName(newDirName)) {
            UserDirectory renamedDirectory = userDirectoryService.renameDirectory(directory, newDirName, user);
            return new ResponseEntity<>(EntityToDtoConverter.convert(renamedDirectory), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(value = "/directories/share", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDirectory(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(shared.getDocId());

        if (directoryAccessService.isOwnerOfActual(directory, user)) {
            UserDirectory sharedDirectory = userDirectoryService.shareDirectory(directory, shared, user);
            return ResponseEntity.ok(EntityToDtoConverter.convert(sharedDirectory));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @RequestMapping(value = "/directories/{dirHashName}/content", method = RequestMethod.GET)
    public ResponseEntity<Set<UserFileDto>> getDirectoryContent(@PathVariable String dirHashName, HttpSession session) {
        User user = getUserFromSession(session);
        if (dirHashName.equals("root")) {
            dirHashName = user.getLogin();
            return ResponseEntity.ok(userDirectoryService.getDirectoryContent(dirHashName));
        }
        UserDirectory directory = userDirectoryService.getByHashName(dirHashName);
        if (directoryAccessService.canRead(directory, user)) {
            return ResponseEntity.ok(userDirectoryService.getDirectoryContent(dirHashName));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @RequestMapping(value = "/directories/{dirHashName}/parent/content", method = RequestMethod.GET)
    public ResponseEntity<Set<UserFileDto>> getParentDirectoryContent(@PathVariable String dirHashName,
                                                                      HttpSession session) {

        User user = getUserFromSession(session);
        UserDirectory currentDirectory = userDirectoryService.getByHashName(dirHashName);
        if (directoryAccessService.canRead(currentDirectory, user)) {
            String parentDirectoryHash = currentDirectory.getParentDirectoryHash();
            return ResponseEntity.ok(userDirectoryService.getDirectoryContent(parentDirectoryHash));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
}
