package com.geekhub.controllers;

import com.geekhub.controllers.utils.FileControllersUtil;
import com.geekhub.dto.DocumentOldVersionDto;
import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.dto.SharedDto;
import com.geekhub.dto.UserDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.exceptions.ResourceNotFoundException;
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.DocumentOldVersionService;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.RemovedDocumentService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.services.impl.EventSendingService;
import com.geekhub.utils.DocumentVersionUtil;
import com.geekhub.utils.UserFileUtil;
import com.geekhub.validators.FileValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/document")
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
    private RemovedDocumentService removedDocumentService;

    @Inject
    private FriendsGroupService friendsGroupService;

    @Inject
    private DocumentOldVersionService documentOldVersionService;

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

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView uploadDocument(HttpSession session) {
        ModelAndView model = new ModelAndView("home");
        User user = getUserFromSession(session);

        Set<FriendsGroupDto> friendsGroups = new TreeSet<>();
        userService.getAllFriendsGroups(user.getId()).forEach(g -> friendsGroups.add(EntityToDtoConverter.convert(g)));

        Set<UserDto> friends = new HashSet<>();
        userService.getAllFriends(user.getId()).forEach(f -> friends.add(EntityToDtoConverter.convert(f)));

        model.addObject("tableNames", new String[] {"ALL", "PRIVATE", "PUBLIC", "FOR_FRIENDS"});
        model.addObject("friendsGroups", friendsGroups);
        model.addObject("friends", friends);
        model.addObject("userLogin", user.getLogin());
        return model;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
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

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty() && UserFileUtil.validateDocumentName(file.getOriginalFilename())) {
                    saveOrUpdateDocument(file, directory, description, user);
                }
            }
        }
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping(value = {"/download/{docId}", "/download-{docId}"}, method = RequestMethod.GET)
    public void downloadDocument(@PathVariable long docId, HttpSession session, HttpServletResponse response)
            throws IOException {

        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

        if (documentAccessService.canRead(document, user)) {
            FileControllersUtil.openOutputStream(document.getHashName(), document.getType(), document.getName(), response);
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping("/set_comment_ability")
    public void setCommentAbility(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

        if (documentAccessService.isOwner(document, user)) {
            boolean abilityToComment = document.getAbilityToComment() == AbilityToCommentDocument.ENABLE;
            document.setAbilityToComment(abilityToComment
                    ? AbilityToCommentDocument.DISABLE
                    : AbilityToCommentDocument.ENABLE);
            userDocumentService.update(document);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = {"/browse/{docId}", "/browse-{docId}"}, method = RequestMethod.GET)
    public ModelAndView browseDocument(@PathVariable long docId, HttpSession session) {
        ModelAndView model = new ModelAndView();
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessService.canRead(document, user)) {
            boolean abilityToComment = document.getAbilityToComment() == AbilityToCommentDocument.ENABLE;
            model.setViewName("document");
            model.addObject("doc", EntityToDtoConverter.convert(document));
            model.addObject("location", userDocumentService.getLocation(document));
            model.addObject("renderSettings", documentAccessService.isOwner(document, user) || abilityToComment);
            model.addObject("renderComments", abilityToComment);
            if (documentAccessService.isOwner(document, user)) {
                model.addObject("historyLink", "/document/history/" + document.getId());
                String action = abilityToComment
                        ? "Disable comments for this file"
                        : "Enable comments for this file";
                model.addObject("changeAbilityToComment", action);
            }
            return model;
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/get_document", method = RequestMethod.GET)
    public UserFileDto getUserDocument(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            return EntityToDtoConverter.convert(document);
        }
        return null;
    }

    @RequestMapping(value = "/rename_document", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> renameDocument(Long docId, String newDocName, HttpSession session) {
        UserDocument document = userDocumentService.getById(docId);
        User user = getUserFromSession(session);
        newDocName = newDocName + document.getExtension();

        if (document.getName().equals(newDocName)) {
            return null;
        }

        if (documentAccessService.isOwner(document, user)) {
            UserDocument documentWithNewName =
                    userDocumentService.getByFullNameAndOwner(user, document.getParentDirectoryHash(), newDocName);
            if (documentWithNewName == null && UserFileUtil.validateDocumentNameWithoutExtension(newDocName)) {
                String oldDocName = document.getName();
                document.setName(newDocName);
                userDocumentService.update(document);

                eventSendingService.sendRenameEvent(userDocumentService
                        .getAllReadersAndEditors(docId), "document", oldDocName, newDocName, document.getId(), user);

                return new ResponseEntity<>(EntityToDtoConverter.convert(document), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/share_document", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDocument(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(shared.getDocId());
        Set<User> currentReadersAndEditors = userDocumentService.getAllReadersAndEditors(document.getId());

        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            document.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));
            document.setReaders(FileControllersUtil.createEntitySet(shared.getReaders(), userService));
            document.setEditors(FileControllersUtil.createEntitySet(shared.getEditors(), userService));
            document.setReadersGroups(FileControllersUtil.createEntitySet(shared.getReadersGroups(), friendsGroupService));
            document.setEditorsGroups(FileControllersUtil.createEntitySet(shared.getEditorsGroups(), friendsGroupService));
            userDocumentService.update(document);

            Set<User> newReadersAndEditorsSet = userDocumentService.getAllReadersAndEditors(document.getId());
            newReadersAndEditorsSet.removeAll(currentReadersAndEditors);
            eventSendingService
                    .sendShareEvent(newReadersAndEditorsSet, "document", document.getName(), document.getId(), user);

            newReadersAndEditorsSet = userDocumentService.getAllReadersAndEditors(document.getId());
            currentReadersAndEditors.removeAll(newReadersAndEditorsSet);
            eventSendingService.sendProhibitAccessEvent(currentReadersAndEditors, "document", document.getName(), user);

            return new ResponseEntity<>(EntityToDtoConverter.convert(document), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping("/history/{docId}")
    public ModelAndView showHistory(@PathVariable Long docId, HttpSession session) {
        ModelAndView model = new ModelAndView("history");
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getWithOldVersions(docId);
        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            List<DocumentOldVersionDto> versions = new ArrayList<>();
            document.getDocumentOldVersions().forEach(v -> versions.add(EntityToDtoConverter.convert(v)));
            model.addObject("versions", versions);
            return model;
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping("/version_recover/{oldVersionId}")
    public ModelAndView recoverVersion(@PathVariable Long oldVersionId, HttpSession session) {
        User user = getUserFromSession(session);
        DocumentOldVersion oldVersion = documentOldVersionService.getById(oldVersionId);
        UserDocument document = oldVersion.getUserDocument();

        if (documentAccessService.isOwner(document, user)) {
            DocumentOldVersion currentVersion = DocumentVersionUtil.createOldVersion(document);
            document.getDocumentOldVersions().add(currentVersion);
            document = DocumentVersionUtil.recoverOldVersion(oldVersion);
            userDocumentService.update(document);
            documentOldVersionService.delete(oldVersion);
            return new ModelAndView("redirect:/document/upload");
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/download_old_version/{versionId}", method = RequestMethod.GET)
    public void downloadDocumentOldVersion(@PathVariable long versionId,
                                           HttpSession session,
                                           HttpServletResponse response) throws IOException {

        User user = getUserFromSession(session);
        DocumentOldVersion oldVersion = documentOldVersionService.getById(versionId);
        UserDocument document = oldVersion.getUserDocument();

        if (documentAccessService.canRead(document, user)) {
            FileControllersUtil
                    .openOutputStream(oldVersion.getHashName(), document.getType(), oldVersion.getName(), response);
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping("/accessible")
    public ModelAndView getAccessibleDocuments(HttpSession session) {
        User user = getUserFromSession(session);
        Set<UserFileDto> documentDtos = new TreeSet<>();
        userDocumentService.getAllCanRead(user).forEach(d -> documentDtos.add(EntityToDtoConverter.convert(d)));
        ModelAndView model = new ModelAndView("accessibleDocuments");
        model.addObject("documents", documentDtos);
        return model;
    }

    private void saveOrUpdateDocument(MultipartFile multipartFile,
                                      UserDirectory directory,
                                      String description,
                                      User user) throws IOException {

        String docName = multipartFile.getOriginalFilename();
        String parentDirectoryHash = directory == null ? user.getLogin() : directory.getHashName();
        UserDocument document = userDocumentService.getByFullNameAndOwner(user, parentDirectoryHash, docName);

        if (document == null) {
            document = UserFileUtil.createUserDocument(multipartFile, directory, description, user);
            multipartFile.transferTo(UserFileUtil.createFile(document.getHashName()));
            userDocumentService.save(document);
        } else if (document.getDocumentStatus() == DocumentStatus.REMOVED) {
            RemovedDocument removedDocument = removedDocumentService.getByUserDocument(document);
            Long docId = userDocumentService.recover(removedDocument.getId());
            document = userDocumentService.getDocumentWithOldVersions(docId);
            updateDocument(document, user, description, multipartFile);
        } else if (documentAccessService.canEdit(document, user)) {
            document = userDocumentService.getDocumentWithOldVersions(document.getId());
            updateDocument(document, user, description, multipartFile);
        }
    }

    private void updateDocument(UserDocument document, User user, String description, MultipartFile multipartFile)
            throws IOException {

        DocumentOldVersion oldVersion = DocumentVersionUtil.createOldVersion(document);
        document.getDocumentOldVersions().add(oldVersion);
        userDocumentService.update(UserFileUtil.updateUserDocument(document, multipartFile, description, user));
        eventSendingService.sendUpdateEvent(document, user);
    }
}
