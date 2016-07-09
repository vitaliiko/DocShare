package com.geekhub.controllers;

import com.geekhub.controllers.utils.FileControllersUtil;
import com.geekhub.dto.*;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.exceptions.ResourceNotFoundException;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.DocumentOldVersionService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.utils.UserFileUtil;
import com.geekhub.validators.FileValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserDocumentController {

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private FileValidator fileValidator;

    @Inject
    private DocumentOldVersionService documentOldVersionService;

    @Inject
    private UserDirectoryAccessService directoryAccessService;

    @Inject
    private UserDocumentAccessService documentAccessService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public ModelAndView createUploadDocumentPageModel(HttpSession session) {
        ModelAndView model = new ModelAndView("home");
        User user = getUserFromSession(session);

        List<FriendsGroup> friendsGroups = userService.getAllFriendsGroups(user.getId());
        Set<FriendsGroupDto> friendsGroupDtoSet = friendsGroups.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toSet());

        List<User> friends = userService.getAllFriends(user.getId());
        Set<UserDto> friendsDtoSet = friends.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toSet());

        model.addObject("tableNames", FileControllersUtil.ACCESS_ATTRIBUTES);
        model.addObject("friendsGroups", friendsGroupDtoSet);
        model.addObject("friends", friendsDtoSet);
        model.addObject("userLogin", user.getLogin());
        return model;
    }

    @RequestMapping(value = "/documents/upload", method = RequestMethod.POST)
    public ModelAndView uploadDocument(@RequestParam("files[]") MultipartFile[] files,
                                       @RequestParam(required = false, name = "dirHashName") String parentDirectoryHash,
                                       String description,
                                       HttpSession session) throws IOException {

        User user = getUserFromSession(session);
        UserDirectory directory = null;
        if (parentDirectoryHash != null && !parentDirectoryHash.isEmpty()) {
            directory = userDirectoryService.getByHashName(parentDirectoryHash);
            if (!directoryAccessService.isOwner(directory, user)) {
                throw new ResourceNotFoundException();
            }
        }

        if (UserFileUtil.isValidFileUploading(files)) {
            for (MultipartFile file : files) {
                userDocumentService.saveOrUpdateDocument(file, directory, description, user);
            }
        }

        return new ModelAndView("redirect:/api/documents");
    }

    @RequestMapping(value = "/documents/{docId}/download", method = RequestMethod.GET)
    public ResponseEntity downloadDocument(@PathVariable long docId,
                                           HttpSession session, HttpServletResponse response) throws IOException {

        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

        if (documentAccessService.canRead(document, user)) {
            openOutputStream(document, response);
            return ResponseEntity.ok().build();
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/documents/{docId}/comment-ability", method = RequestMethod.POST)
    public ResponseEntity setCommentAbility(@PathVariable long docId,
                                            @RequestParam boolean abilityToComment,
                                            HttpSession session) {

        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

        if (!documentAccessService.isOwner(document, user)) {
            throw new ResourceNotFoundException();
        }
        userDocumentService.changeAbilityToComment(document, abilityToComment);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/documents/{docId}/browse", method = RequestMethod.GET)
    public ModelAndView browseDocument(@PathVariable long docId, HttpSession session) {
        ModelAndView model = new ModelAndView();
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

        if (!documentAccessService.canRead(document, user)) {
            throw new ResourceNotFoundException();
        }

        boolean abilityToComment = AbilityToCommentDocument.getBoolean(document.getAbilityToComment());
        model.setViewName("document");
        model.addObject("doc", EntityToDtoConverter.convert(document));
        model.addObject("location", userDocumentService.getLocation(document));
        model.addObject("renderSettings", documentAccessService.isOwner(document, user) || abilityToComment);
        model.addObject("renderComments", abilityToComment);
        if (documentAccessService.isOwner(document, user)) {
            model.addObject("historyLink", "/api/documents/" + document.getId() + "/history");
            model.addObject("abilityToComment", abilityToComment);
        }
        return model;
    }

    @RequestMapping(value = "/documents/{docId}", method = RequestMethod.GET)
    public ResponseEntity<UserFileDto> getUserDocument(@PathVariable long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            UserFileDto fileDto = EntityToDtoConverter.convert(document);
            return ResponseEntity.ok().body(fileDto);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(value = "/documents/{docId}/rename", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> renameDocument(@PathVariable Long docId,
                                                      @RequestParam String newDocName,
                                                      HttpSession session) {

        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        newDocName = newDocName + document.getExtension();

        if (document.getName().equals(newDocName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        if (!documentAccessService.isOwner(document, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        UserDocument existingDocument =
                userDocumentService.getByFullNameAndOwner(user, document.getParentDirectoryHash(), newDocName);
        if (existingDocument == null && UserFileUtil.validateDocumentNameWithoutExtension(newDocName)) {
            UserDocument documentWithNewName = userDocumentService.renameDocument(document, newDocName, user);
            return ResponseEntity.ok(EntityToDtoConverter.convert(documentWithNewName));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(value = "/documents/share", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDocument(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(shared.getDocId());

        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            UserDocument sharedDocument = userDocumentService.shareDocument(document, shared, user);
            return ResponseEntity.ok(EntityToDtoConverter.convert(sharedDocument));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(value = "/documents/{docId}/history", method = RequestMethod.GET)
    public ModelAndView showHistory(@PathVariable Long docId, HttpSession session) {
        ModelAndView model = new ModelAndView("history");
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getWithOldVersions(docId);
        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            List<DocumentOldVersionDto> versions = document.getDocumentOldVersions().stream()
                    .map(EntityToDtoConverter::convert)
                    .collect(Collectors.toList());
            model.addObject("versions", versions);
            return model;
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/documents/versions/{oldVersionId}/recover", method = RequestMethod.POST)
    public ModelAndView recoverVersion(@PathVariable Long oldVersionId, HttpSession session) {
        User user = getUserFromSession(session);
        DocumentOldVersion oldVersion = documentOldVersionService.getById(oldVersionId);
        UserDocument document = oldVersion.getUserDocument();

        if (documentAccessService.isOwner(document, user)) {
            userDocumentService.recoverOldVersion(oldVersion);
            return new ModelAndView("redirect:/api/documents");
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/documents/versions/{versionId}/download", method = RequestMethod.GET)
    public ResponseEntity downloadDocumentOldVersion(@PathVariable long versionId,
                                                     HttpSession session,
                                                     HttpServletResponse response) throws IOException {

        User user = getUserFromSession(session);
        DocumentOldVersion oldVersion = documentOldVersionService.getById(versionId);
        UserDocument document = oldVersion.getUserDocument();

        if (documentAccessService.canRead(document, user)) {
            openOutputStream(oldVersion.getHashName(), document.getType(), oldVersion.getName(), response);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @RequestMapping(value = "/documents/accessible", method = RequestMethod.GET)
    public ModelAndView getAccessibleDocuments(HttpSession session) {
        User user = getUserFromSession(session);
        Set<UserFileDto> documentDtos = userDocumentService.getAllCanRead(user).stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toCollection(TreeSet::new));
        ModelAndView model = new ModelAndView("accessibleDocuments");
        model.addObject("documents", documentDtos);
        return model;
    }

    @RequestMapping(value = "/documents/{removedDocId}/recover", method = RequestMethod.POST)
    public ModelAndView recoverDocument(@PathVariable long removedDocId, HttpSession session) {
        User user = getUserFromSession(session);
        if (documentAccessService.canRecover(removedDocId, user)) {
            userDocumentService.recoverRemovedDocument(removedDocId, user);
            return new ModelAndView("redirect:/api/documents");
        }
        throw new ResourceNotFoundException();
    }

    private static void openOutputStream(UserDocument document, HttpServletResponse response) throws IOException {
        openOutputStream(document.getHashName(), document.getType(), document.getName(), response);
    }

    private static void openOutputStream(String docHashName, String docType, String docName, HttpServletResponse response)
            throws IOException {

        File file = UserFileUtil.createFile(docHashName);
        response.setContentType(docType);
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\"");

        FileCopyUtils.copy(Files.newInputStream(file.toPath()), response.getOutputStream());
    }
}
