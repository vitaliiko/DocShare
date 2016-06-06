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
import com.geekhub.utils.DocumentVersionUtil;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.utils.UserFileUtil;
import com.geekhub.validators.FileValidator;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private UserDirectoryService userDirectoryService;

    @Autowired
    private FileValidator fileValidator;

    @Autowired
    private RemovedDocumentService removedDocumentService;

    @Autowired
    private RemovedDirectoryService removedDirectoryService;

    @Autowired
    private FriendsGroupService friendsGroupService;

    @Autowired
    private DocumentOldVersionService documentOldVersionService;

    @Autowired
    private UserDirectoryAccessService directoryAccessService;

    @Autowired
    private UserDocumentAccessService documentAccessService;

    @Autowired
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
            openOutputStream(document.getHashName(), document.getType(), document.getName(), response);
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

    @RequestMapping(value = "/recover_directory", method = RequestMethod.POST)
    public ModelAndView recoverDirectory(long remDirId, HttpSession session) {
        User user = getUserFromSession(session);
        if (directoryAccessService.canRecover(remDirId, user)) {
            Long dirId = userDirectoryService.recover(remDirId);

            String dirName = userDirectoryService.getById(dirId).getName();
            eventSendingService.sendRecoverEvent(userDirectoryService, "Directory", dirName, dirId, user);
            return new ModelAndView("redirect:/document/upload");
        }
        throw new ResourceNotFoundException();
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

    @RequestMapping(value = "/make-directory", method = RequestMethod.GET)
    public ResponseEntity<UserFileDto> makeDir(String dirName,
                                               @RequestParam(required = false, name = "dirHashName") String parentDirHash,
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

    @RequestMapping(value = "/get_document", method = RequestMethod.GET)
    public UserFileDto getUserDocument(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            return EntityToDtoConverter.convert(document);
        }
        return null;
    }

    @RequestMapping("/get_directory")
    public UserFileDto getUserDirectory(Long dirId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(dirId);
        if (directoryAccessService.isOwner(directory, user)
                && directory.getDocumentStatus() == DocumentStatus.ACTUAL) {
            return EntityToDtoConverter.convert(directory);
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

    @RequestMapping(value = "/rename_directory", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> renameDirectory(Long dirId, String newDirName, HttpSession session) {
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

    @RequestMapping(value = "/share_document", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDocument(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(shared.getDocId());
        Set<User> currentReadersAndEditors = userDocumentService.getAllReadersAndEditors(document.getId());

        if (documentAccessService.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            document.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));
            document.setReaders(createEntitySet(shared.getReaders(), userService));
            document.setEditors(createEntitySet(shared.getEditors(), userService));
            document.setReadersGroups(createEntitySet(shared.getReadersGroups(), friendsGroupService));
            document.setEditorsGroups(createEntitySet(shared.getEditorsGroups(), friendsGroupService));
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

    @RequestMapping(value = "/share_directory", method = RequestMethod.POST)
    public ResponseEntity<UserFileDto> shareUserDirectory(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(shared.getDocId());
        Set<User> currentReaders = userDirectoryService.getAllReaders(directory.getId());

        if (directoryAccessService.isOwner(directory, user)
                && directory.getDocumentStatus() == DocumentStatus.ACTUAL) {
            directory.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));

            Set<User> readers = createEntitySet(shared.getReaders(), userService);
            Set<FriendsGroup> readerGroups = createEntitySet(shared.getReadersGroups(), friendsGroupService);

            directory.setReaders(readers);
            directory.setReadersGroups(readerGroups);

            userDocumentService.getActualByParentDirectoryHash(directory.getHashName()).forEach(d -> {
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
            openOutputStream(oldVersion.getHashName(), document.getType(), oldVersion.getName(), response);
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

    @RequestMapping("/get-directory-content-{dirHashName}")
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

    @RequestMapping("get-parent-directory-content-{dirHashName}")
    public ResponseEntity<Set<UserFileDto>> getParentDirectoryContent(@PathVariable String dirHashName,
                                                                      HttpSession session) {

        User user = getUserFromSession(session);
        UserDirectory currentDirectory = userDirectoryService.getByHashName(dirHashName);
        if (directoryAccessService.canRead(currentDirectory, user)) {
            return new ResponseEntity<>(getDirectoryContent(currentDirectory.getParentDirectoryHash()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

    private void openOutputStream(String docHashName, String docType, String docName, HttpServletResponse response)
            throws IOException {

        File file = UserFileUtil.createFile(docHashName);
        response.setContentType(docType);
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\"");

        FileCopyUtils.copy(Files.newInputStream(file.toPath()), response.getOutputStream());
    }

    private <T, S extends EntityService<T, Long>> Set<T> createEntitySet(long[] ids, S service) {
        Set<T> entitySet = new HashSet<>();
        Arrays.stream(ids).forEach(id -> entitySet.add(service.getById(id)));
        return entitySet;
    }

    private Set<UserFileDto> getDirectoryContent(String directoryHashName) {
        List<UserDocument> documents;
        List<UserDirectory> directories;
        documents = userDocumentService.getActualByParentDirectoryHash(directoryHashName);
        directories = userDirectoryService.getActualByParentDirectoryHash(directoryHashName);

        Set<UserFileDto> dtoSet = new TreeSet<>();
        if (documents != null) {
            documents.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        if (directories != null) {
            directories.forEach(d -> dtoSet.add(EntityToDtoConverter.convert(d)));
        }
        return dtoSet;
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
}