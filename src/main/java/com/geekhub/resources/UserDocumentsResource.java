package com.geekhub.resources;

import com.geekhub.resources.utils.FileControllersUtil;
import com.geekhub.dto.*;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.*;
import com.geekhub.utils.UserFileUtil;
import com.geekhub.validators.FileValidator;
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
    private FileValidator fileValidator;

    @Inject
    private DocumentOldVersionService documentOldVersionService;

    @Inject
    private UserDocumentAccessService documentAccessService;

    @Inject
    private UserToDirectoryRelationService userToDirectoryRelationService;

    @Inject
    private UserToDocumentRelationService userToDocumentRelationService;

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

    @RequestMapping(value = "/documents/{docId}/download", method = RequestMethod.GET)
    public ResponseEntity downloadDocument(@PathVariable Long docId,
                                           HttpServletResponse response) throws IOException {

        UserDocument document = userDocumentService.getById(docId);
        openOutputStream(document, response);
        return ResponseEntity.ok().build();
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

    @RequestMapping(value = "/documents/{docId}/comment-ability", method = RequestMethod.POST)
    public ResponseEntity setCommentAbility(@PathVariable Long docId,
                                            @RequestParam boolean abilityToComment) {

        userDocumentService.changeAbilityToComment(docId, abilityToComment);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/documents/{docId}", method = RequestMethod.GET)
    public ModelAndView browseDocument(@PathVariable Long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

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

    @RequestMapping(value = "/documents/{docId}/access", method = RequestMethod.GET)
    public ResponseEntity<FileAccessDto> getUserDocument(@PathVariable Long docId) {
        FileAccessDto accessDto = userDocumentService.findAllRelations(docId);
        return ResponseEntity.ok().body(accessDto);
    }

    @RequestMapping(value = "/documents/{docId}/rename", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> renameDocument(@PathVariable Long docId,
                                                      @RequestParam String newDocName,
                                                      HttpSession session) {

        UserDocument document = userDocumentService.getById(docId);
        newDocName = newDocName + document.getExtension();
        User user = getUserFromSession(session);
        if (document.getName().equals(newDocName)
                || !userDocumentService.isDocumentNameValid(document.getParentDirectoryHash(), newDocName, user)) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDocument documentWithNewName = userDocumentService.renameDocument(document, newDocName, user);
        return ResponseEntity.ok(EntityToDtoConverter.convert(documentWithNewName));
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
        return prepareModel(user, document);
    }

    private ModelAndView prepareModel(User user, UserDocument document) {
        ModelAndView model = new ModelAndView("history");
        List<DocumentOldVersionDto> versions = EntityToDtoConverter.convertToVersionDtos(document, user);
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
                                       @PathVariable Long oldVersionId) {

        DocumentOldVersion oldVersion = documentOldVersionService.getById(oldVersionId);
        userDocumentService.recoverOldVersion(oldVersion);
        return new ModelAndView("redirect:/api/documents");
    }

    @RequestMapping(value = "/documents/{docId}/versions/{versionId}/download", method = RequestMethod.GET)
    public ResponseEntity downloadDocumentOldVersion(@PathVariable Long docId,
                                                     @PathVariable Long versionId,
                                                     HttpServletResponse response) throws IOException {

        DocumentOldVersion oldVersion = documentOldVersionService.getById(versionId);
        UserDocument document = oldVersion.getUserDocument();
        openOutputStream(oldVersion.getHashName(), document.getType(), oldVersion.getName(), response);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/documents/{docId}/recover", method = RequestMethod.POST)
    public ModelAndView recoverDocument(@PathVariable Long docId, HttpSession session) {
        User user = getUserFromSession(session);
        userDocumentService.recoverRemovedDocument(docId, user);
        return new ModelAndView("redirect:/api/documents");
    }
}
