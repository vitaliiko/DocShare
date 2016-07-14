package com.geekhub.resources;

import com.geekhub.dto.RemovedFileDto;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.dto.UserFileDto;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import com.geekhub.services.EventSendingService;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.validators.FileValidator;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class FilesResource {

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private FileValidator fileValidator;

    @Inject
    private RemovedDocumentService removedDocumentService;

    @Inject
    private RemovedDirectoryService removedDirectoryService;

    @Inject
    private UserDirectoryAccessService directoryAccessService;

    @Inject
    private UserDocumentAccessService documentAccessService;

    @Inject
    private UserDirectoryAccessService userDirectoryAccessService;

    @Inject
    private EventSendingService eventSendingService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/files/search", method = RequestMethod.GET)
    public ResponseEntity<Set<UserFileDto>> searchFiles(@RequestParam String searchName, HttpSession session) {
        User user = getUserFromSession(session);
        Set<UserDocument> documents = null;
        Set<UserDirectory> directories = null;

        if (searchName != null) {
            documents = userDocumentService.searchByName(user, searchName);
            directories = userDirectoryService.searchByName(user, searchName);
        }

        Set<UserFileDto> dtoSet = new TreeSet<>();
        if (documents != null) {
            documents.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        if (directories != null) {
            directories.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        return ResponseEntity.ok(dtoSet);
    }

    @RequestMapping(value = "/files/replace", method = RequestMethod.POST)
    public ResponseEntity<Void> replaceFiles(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                             @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                             @RequestParam String destinationDirHash,
                                             HttpSession session) {

        User user = getUserFromSession(session);

        if (docIds != null && !userDocumentService.replace(docIds, destinationDirHash, user)) {
            return ResponseEntity.badRequest().build();
        }
        if (dirIds != null && !userDirectoryService.replace(dirIds, destinationDirHash, user)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/files/copy", method = RequestMethod.POST)
    public ResponseEntity<Void> copyFiles(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                          @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                          @RequestParam String destinationDirHash,
                                          HttpSession session) {

        User user = getUserFromSession(session);

        if (destinationDirHash.equals("root")) {
            destinationDirHash = user.getLogin();
        }
        if (!userDirectoryAccessService.isOwner(destinationDirHash, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (docIds != null && !userDocumentService.copy(docIds, destinationDirHash, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (dirIds != null && !userDirectoryService.copy(dirIds, destinationDirHash, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/files/move-to-trash", method = RequestMethod.POST)
    public ResponseEntity moveDocumentToTrash(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                              @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                              HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        User user = getUserFromSession(session);
        if (docIds != null) {
            Set<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessService.canRemove(documents, user)) {
                userDocumentService.moveToTrash(docIds, userId);
                documents.forEach(doc -> eventSendingService
                        .sendRemoveEvent(userDocumentService, FileType.DOCUMENT, doc.getName(), doc.getId(), user));
            }
        }
        if (dirIds != null) {
            Set<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessService.canRemove(directories, user)) {
                userDirectoryService.moveToTrash(dirIds, userId);
                directories.forEach(dir -> eventSendingService
                        .sendRemoveEvent(userDirectoryService, FileType.DIRECTORY, dir.getName(), dir.getId(), user));
            }
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/files/removed", method = RequestMethod.GET)
    public ModelAndView recoverDocument(HttpSession session) {
        ModelAndView model = new ModelAndView("recover");

        Long ownerId = (Long) session.getAttribute("userId");
        Set<RemovedFileDto> documents = new TreeSet<>();
        removedDocumentService.getAllByOwnerId(ownerId).forEach(d -> {
            User user = userService.getById(d.getRemoverId());
            documents.add(EntityToDtoConverter.convert(d, user.getFullName()));
        });

        Set<RemovedFileDto> directories = new TreeSet<>();
        removedDirectoryService.getAllByOwnerId(ownerId).forEach(d -> {
            User user = userService.getById(d.getRemoverId());
            directories.add(EntityToDtoConverter.convert(d, user.getFullName()));
        });

        model.addObject("documents", documents);
        model.addObject("directories", directories);
        return model;
    }
}