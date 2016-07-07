package com.geekhub.controllers;

import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.dto.RemovedFileDto;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.DocumentOldVersionDto;
import com.geekhub.dto.SharedDto;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.exceptions.ResourceNotFoundException;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.DocumentOldVersionService;
import com.geekhub.services.EntityService;
import com.geekhub.services.impl.EventSendingService;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.RemovedDirectoryService;
import com.geekhub.services.RemovedDocumentService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
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
@RequestMapping("/document")
public class FilesController {

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
    private EventSendingService eventSendingService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping("/search_files")
    public Set<UserFileDto> searchFiles(String searchName, HttpSession session) {
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
        return dtoSet;
    }

    @RequestMapping(value = "/replace_files", method = RequestMethod.POST)
    public ResponseEntity<Void> replaceFiles(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                             @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                             String destinationDirHash,
                                             HttpSession session) {

        User user = getUserFromSession(session);
        if (docIds != null && destinationDirHash != null) {
            Set<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessService.isOwner(documents, user)) {
                userDocumentService.replace(docIds, destinationDirHash);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        if (dirIds != null && destinationDirHash != null) {
            Set<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessService.isOwner(directories, user)) {
                userDirectoryService.replace(dirIds, destinationDirHash);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/copy_files", method = RequestMethod.POST)
    public ResponseEntity<Void> copyFiles(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                          @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                          String destinationDirHash,
                                          HttpSession session) {

        User user = getUserFromSession(session);
        if (destinationDirHash == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (destinationDirHash.equals("root")) {
            destinationDirHash = user.getLogin();
            if (docIds != null) {
                userDocumentService.copy(docIds, user.getLogin());
            }
            if (dirIds != null) {
                userDirectoryService.copy(dirIds, destinationDirHash);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }

        if (docIds != null) {
            Set<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessService.isOwner(documents, user)) {
                userDocumentService.copy(docIds, destinationDirHash);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        if (dirIds != null) {
            Set<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessService.isOwner(directories, user)) {
                userDirectoryService.copy(dirIds, destinationDirHash);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/move_to_trash", method = RequestMethod.POST)
    public void moveDocumentToTrash(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                    @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                    HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        User user = getUserFromSession(session);
        if (docIds != null) {
            Set<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessService.canRemove(documents, user)) {
                userDocumentService.moveToTrash(docIds, userId);
                documents.forEach(doc -> eventSendingService
                        .sendRemoveEvent(userDocumentService, "Document", doc.getName(), doc.getId(), user));
            }
        }
        if (dirIds != null) {
            Set<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessService.canRemove(directories, user)) {
                userDirectoryService.moveToTrash(dirIds, userId);
                directories.forEach(dir -> eventSendingService
                        .sendRemoveEvent(userDirectoryService, "Directory", dir.getName(), dir.getId(), user));
            }
        }
    }

    @RequestMapping(value = "/recover", method = RequestMethod.GET)
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

    @RequestMapping(value = "/recover_document", method = RequestMethod.POST)
    public ModelAndView recoverDocument(long remDocId, HttpSession session) {
        User user = getUserFromSession(session);
        if (documentAccessService.canRecover(remDocId, user)) {
            Long docId = userDocumentService.recover(remDocId);

            String docName = userDocumentService.getById(docId).getName();
            eventSendingService.sendRecoverEvent(userDocumentService, "Document", docName, docId, user);
            return new ModelAndView("redirect:/document/upload");
        }
        throw new ResourceNotFoundException();
    }

}