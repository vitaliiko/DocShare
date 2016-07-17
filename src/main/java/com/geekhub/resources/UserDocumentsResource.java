package com.geekhub.resources;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.resources.utils.FileControllersUtil;
import com.geekhub.dto.*;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.exceptions.ResourceNotFoundException;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.*;
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
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserDocumentsResource {

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

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

    @Inject
    private FriendGroupToDocumentRelationService friendGroupToDocumentRelationService;

    private User getUserFromSession(HttpSession session) {
        return userService.getById((Long) session.getAttribute("userId"));
    }

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public ModelAndView createUploadDocumentPageModel(HttpSession session) {
        User user = getUserFromSession(session);
        return prepareModel(user);
    }

    private ModelAndView prepareModel(User user) {
        ModelAndView model = new ModelAndView("home");
        List<FriendsGroup> friendsGroups = userService.getAllFriendsGroups(user.getId());
        Set<FriendGroupDto> friendGroupDtoSet = friendsGroups.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toSet());

        List<User> friends = userService.getAllFriends(user.getId());
        Set<UserDto> friendsDtoSet = friends.stream()
                .map(EntityToDtoConverter::convert)
                .collect(Collectors.toSet());

        model.addObject("tableNames", FileControllersUtil.ACCESS_ATTRIBUTES);
        model.addObject("friendsGroups", friendGroupDtoSet);
        model.addObject("friends", friendsDtoSet);
        model.addObject("userLogin", user.getLogin());
        return model;
    }

    @RequestMapping(value = "/documents/upload", method = RequestMethod.POST)
    public ModelAndView uploadDocument(@RequestParam("files[]") MultipartFile[] files,
                                       @RequestParam(required = false, name = "dirHashName") String parentDirectoryHash,
                                       HttpSession session) throws IOException {

        User user = getUserFromSession(session);
        UserDirectory parentDirectory = null;
        if (parentDirectoryHash != null && !parentDirectoryHash.isEmpty()) {
            parentDirectory = userDirectoryService.getByHashName(parentDirectoryHash);
            if (!directoryAccessService.isOwnerOfActual(parentDirectory, user)) {
                throw new ResourceNotFoundException();
            }
        }

        if (UserFileUtil.isValidFileUploading(files)) {
            for (MultipartFile file : files) {
                userDocumentService.saveOrUpdateDocument(file, parentDirectory, user);
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
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

        if (!documentAccessService.canRead(document, user)) {
            throw new ResourceNotFoundException();
        }
        UserFileDto fileDto = EntityToDtoConverter.convert(document);
        if (fileDto.getModifiedBy().equals(user.getFullName())) {
            fileDto.setModifiedBy("Me");
        }
        return prepareModel(fileDto, user, document);
    }

    private ModelAndView prepareModel(UserFileDto fileDto, User user, UserDocument document) {

        boolean abilityToComment = AbilityToCommentDocument.getBoolean(document.getAbilityToComment());
        ModelAndView model = new ModelAndView();
        model.setViewName("document");
        model.addObject("doc", fileDto);
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
        if (documentAccessService.isOwnerOfActual(document, user)) {
            UserFileDto documentDto = EntityToDtoConverter.convert(document);
            documentDto = userDocumentService.findAllRelations(documentDto);
            return ResponseEntity.ok().body(documentDto);
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

        if (!documentAccessService.isOwnerOfActual(document, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        if (document.getName().equals(newDocName)) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDocument existingDocument = userToDocumentRelationService
                .getDocumentByFullNameAndOwner(document.getParentDirectoryHash(), newDocName, user);
        if (existingDocument == null && UserFileUtil.validateDocumentNameWithoutExtension(newDocName)) {
            UserDocument documentWithNewName = userDocumentService.renameDocument(document, newDocName, user);
            return ResponseEntity.ok(EntityToDtoConverter.convert(documentWithNewName));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(value = "/documents/{docId}/share", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDocument(@PathVariable Long docId,
                                                         @Valid @RequestBody SharedDto shared, HttpSession session) {

        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        UserDocument sharedDocument = userDocumentService.shareDocument(document, shared, user);
        return ResponseEntity.ok(EntityToDtoConverter.convert(sharedDocument));
    }

    @RequestMapping(value = "/documents/{docId}/history", method = RequestMethod.GET)
    public ModelAndView showDocumentOldVersions(@PathVariable Long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getWithOldVersions(docId);
        if (documentAccessService.isOwnerOfActual(document, user)) {
            return prepareModel(user, document);
        }
        throw new ResourceNotFoundException();
    }

    private ModelAndView prepareModel(User user, UserDocument document) {
        ModelAndView model = new ModelAndView("history");
        List<DocumentOldVersionDto> versions = document.getDocumentOldVersions().stream()
                .map(EntityToDtoConverter::convert)
                .sorted(DocumentOldVersionDto::compareTo)
                .collect(Collectors.toList());
        versions.stream()
                .filter(dto -> dto.getChangedBy().equals(user.getFullName()))
                .forEachOrdered(dto -> dto.setChangedBy("Me"));
        UserFileDto currentVersionDto = EntityToDtoConverter.convert(document);
        if (currentVersionDto.getModifiedBy().equals(user.getFullName())) {
            currentVersionDto.setModifiedBy("Me");
        }
        model.addObject("versions", versions);
        model.addObject("currentVersion", currentVersionDto);
        return model;
    }

    @RequestMapping(value = "/documents/{docId}/versions/{oldVersionId}/recover", method = RequestMethod.POST)
    public ModelAndView recoverVersion(@PathVariable Long docId,
                                       @PathVariable Long oldVersionId, HttpSession session) {

        User user = getUserFromSession(session);
        DocumentOldVersion oldVersion = documentOldVersionService.getById(oldVersionId);
        UserDocument document = oldVersion.getUserDocument();

        if (documentAccessService.isOwnerOfActual(document, user)) {
            userDocumentService.recoverOldVersion(oldVersion);
            return new ModelAndView("redirect:/api/documents");
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/documents/{docId}/versions/{versionId}/download", method = RequestMethod.GET)
    public ResponseEntity downloadDocumentOldVersion(@PathVariable Long docId,
                                                     @PathVariable Long versionId,
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

        Set<UserDirectory> directories = userToDirectoryRelationService.getAllAccessibleDirectories(user);
        List<String> directoryHashes = directories.stream().map(UserDirectory::getHashName).collect(Collectors.toList());
        Set<UserDocument> documents = userToDocumentRelationService.getAllAccessibleDocumentsInRoot(user, directoryHashes);

        return prepareModel(directories, documents);
    }

    private ModelAndView prepareModel(Set<UserDirectory> directories, Set<UserDocument> documents) {
        Set<UserFileDto> documentDtos = documents.stream()
                .map(EntityToDtoConverter::convert).collect(Collectors.toCollection(TreeSet::new));

        Set<UserFileDto> directoryDtos = directories.stream()
                .map(EntityToDtoConverter::convert).collect(Collectors.toCollection(TreeSet::new));

        ModelAndView model = new ModelAndView("accessibleDocuments");
        model.addObject("documents", documentDtos);
        model.addObject("directories", directoryDtos);
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
