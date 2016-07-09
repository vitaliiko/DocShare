package com.geekhub.controllers;

import com.geekhub.controllers.utils.FileControllersUtil;
import com.geekhub.dto.SharedDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.exceptions.ResourceNotFoundException;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.services.impl.EventSendingService;
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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api")
public class UserDirectoryController {

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private FriendsGroupService friendsGroupService;

    @Inject
    private UserDirectoryAccessService directoryAccessService;

    @Inject
    private EventSendingService eventSendingService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @RequestMapping(value = "/directories/{removedDirId}/recover", method = RequestMethod.POST)
    public ModelAndView recoverDirectory(@PathVariable long removedDirId, HttpSession session) {
        User user = getUserFromSession(session);
        if (directoryAccessService.canRecover(removedDirId, user)) {
            Long dirId = userDirectoryService.recover(removedDirId);

            String dirName = userDirectoryService.getById(dirId).getName();
            eventSendingService.sendRecoverEvent(userDirectoryService, "Directory", dirName, dirId, user);
            return new ModelAndView("redirect:/api/documents");
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/directories", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> makeDir(@RequestParam String dirName,
                                               @RequestParam(required = false) String parentDirHash,
                                               HttpSession session) {

        User owner = getUserFromSession(session);
        if (parentDirHash == null || parentDirHash.isEmpty()) {
            parentDirHash = owner.getLogin();
        }

        if (dirName != null && !dirName.isEmpty()) {
            UserDirectory directory = makeDirectory(owner, parentDirHash, dirName);
            if (directory != null) {
                return new ResponseEntity<>(EntityToDtoConverter.convert(directory), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/directories/{dirId}", method = RequestMethod.GET)
    public UserFileDto getUserDirectory(@PathVariable Long dirId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(dirId);
        if (directoryAccessService.isOwner(directory, user)
                && directory.getDocumentStatus() == DocumentStatus.ACTUAL) {
            return EntityToDtoConverter.convert(directory);
        }
        return null;
    }

    @RequestMapping(value = "/directories/{dirId}/rename", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> renameDirectory(@PathVariable Long dirId,
                                                       @RequestParam String newDirName,
                                                       HttpSession session) {

        UserDirectory directory = userDirectoryService.getById(dirId);
        User user = getUserFromSession(session);

        if (directory.getName().equals(newDirName)) {
            return null;
        }

        if (directoryAccessService.isOwner(directory, user)) {
            UserDirectory directoryWithNewName =
                    userDirectoryService.getByFullNameAndOwner(user, directory.getParentDirectoryHash(), newDirName);
            if (directoryWithNewName == null && UserFileUtil.validateDirectoryName(newDirName)) {
                String oldDirName = directory.getName();
                directory.setName(newDirName);
                userDirectoryService.update(directory);

                eventSendingService.sendRenameEvent(userDirectoryService
                        .getAllReaders(dirId), "directory", oldDirName, newDirName, directory.getId(), user);

                return new ResponseEntity<>(EntityToDtoConverter.convert(directory), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/directories/share", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDirectory(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(shared.getDocId());
        Set<User> currentReaders = userDirectoryService.getAllReaders(directory.getId());

        if (directoryAccessService.isOwner(directory, user)
                && directory.getDocumentStatus() == DocumentStatus.ACTUAL) {
            directory.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));

            Set<User> readers =
                    FileControllersUtil.createEntitySet(shared.getReaders(), userService);
            Set<FriendsGroup> readerGroups =
                    FileControllersUtil.createEntitySet(shared.getReadersGroups(), friendsGroupService);

            directory.setReaders(readers);
            directory.setReadersGroups(readerGroups);

            userDocumentService
                    .getByParentDirectoryHashAndStatus(directory.getHashName(), DocumentStatus.ACTUAL).forEach(d -> {
                d.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));
                d.setReaders(readers);
                d.setReadersGroups(readerGroups);
                userDocumentService.update(d);
            });

            userDirectoryService.update(directory);

            Set<User> newReaderSet = userDirectoryService.getAllReaders(directory.getId());
            newReaderSet.removeAll(currentReaders);
            eventSendingService.sendShareEvent(newReaderSet, "directory", directory.getName(), directory.getId(), user);

            newReaderSet = userDirectoryService.getAllReaders(directory.getId());
            currentReaders.removeAll(newReaderSet);
            eventSendingService.sendProhibitAccessEvent(currentReaders, "directory", directory.getName(), user);

            return new ResponseEntity<>(EntityToDtoConverter.convert(directory), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/directories/{dirHashName}/content", method = RequestMethod.GET)
    public ResponseEntity<Set<UserFileDto>> getDirectoryContent(@PathVariable String dirHashName, HttpSession session) {
        User user = getUserFromSession(session);
        if (dirHashName.equals("root")) {
            dirHashName = user.getLogin();
            return new ResponseEntity<>(getDirectoryContent(dirHashName), HttpStatus.OK);
        } else {
            UserDirectory directory = userDirectoryService.getByHashName(dirHashName);
            if (directoryAccessService.canRead(directory, user)) {
                return new ResponseEntity<>(getDirectoryContent(dirHashName), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/directories/{dirHashName}/parent/content", method = RequestMethod.GET)
    public ResponseEntity<Set<UserFileDto>> getParentDirectoryContent(@PathVariable String dirHashName,
                                                                      HttpSession session) {

        User user = getUserFromSession(session);
        UserDirectory currentDirectory = userDirectoryService.getByHashName(dirHashName);
        if (directoryAccessService.canRead(currentDirectory, user)) {
            return new ResponseEntity<>(getDirectoryContent(currentDirectory.getParentDirectoryHash()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private UserDirectory makeDirectory(User owner, String parentDirectoryHash, String dirName) {
        UserDirectory directory = userDirectoryService.getByFullNameAndOwner(owner, parentDirectoryHash, dirName);

        if (directory == null && UserFileUtil.validateDirectoryName(dirName)) {
            directory = UserFileUtil.createUserDirectory(owner, parentDirectoryHash, dirName);
            long dirId = userDirectoryService.save(directory);
            directory.setId(dirId);
            return directory;
        }
        return null;
    }

    private Set<UserFileDto> getDirectoryContent(String directoryHashName) {
        List<UserDocument> documents;
        List<UserDirectory> directories;
        documents = userDocumentService.getByParentDirectoryHashAndStatus(directoryHashName, DocumentStatus.ACTUAL);
        directories = userDirectoryService.getByParentDirectoryHashAndStatus(directoryHashName, DocumentStatus.ACTUAL);

        Set<UserFileDto> dtoSet = new TreeSet<>();
        if (documents != null) {
            documents.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        if (directories != null) {
            directories.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        return dtoSet;
    }
}
