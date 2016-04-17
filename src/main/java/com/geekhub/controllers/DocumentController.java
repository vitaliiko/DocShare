package com.geekhub.controllers;

import com.geekhub.dto.CommentDto;
import com.geekhub.dto.FriendsGroupDto;
import com.geekhub.dto.RemovedFileDto;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.Comment;
import com.geekhub.entities.DocumentOldVersion;
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
import com.geekhub.security.UserDirectoryAccessService;
import com.geekhub.security.UserDocumentAccessService;
import com.geekhub.services.CommentService;
import com.geekhub.services.DocumentOldVersionService;
import com.geekhub.services.EntityService;
import com.geekhub.services.EventService;
import com.geekhub.services.FriendsGroupService;
import com.geekhub.services.RemovedDirectoryService;
import com.geekhub.services.RemovedDocumentService;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.utils.CommentUtil;
import com.geekhub.utils.DocumentVersionUtil;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.utils.EventUtil;
import com.geekhub.utils.UserFileUtil;
import com.geekhub.validators.FileValidator;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private CommentService commentService;

    @Autowired
    private RemovedDocumentService removedDocumentService;

    @Autowired
    private RemovedDirectoryService removedDirectoryService;

    @Autowired
    private FriendsGroupService friendsGroupService;

    @Autowired
    private DocumentOldVersionService documentOldVersionService;

    @Autowired
    private UserDirectoryAccessService directoryAccessProvider;

    @Autowired
    private UserDocumentAccessService documentAccessProvider;

    @Autowired
    private EventService eventService;

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
        if (parentDirectoryHash == null || parentDirectoryHash.isEmpty()) {
            parentDirectoryHash = user.getLogin();
        }

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty() && UserFileUtil.validateDocumentName(file.getOriginalFilename())) {
                    saveOrUpdateDocument(file, parentDirectoryHash, description, user);
                }
            }
        }
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping(value = "/download-{docId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void downloadDocument(@PathVariable long docId, HttpSession session, HttpServletResponse response)
            throws IOException {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);

        if (documentAccessProvider.canRead(document, user)) {
            File file = UserFileUtil.createFile(document.getHashName());
            response.setContentType(document.getType());
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getName() + "\"");

            FileCopyUtils.copy(Files.newInputStream(file.toPath()), response.getOutputStream());
        }
    }

    @RequestMapping(value = "/move-to-trash", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void moveDocumentToTrash(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                                    @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                                    HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        User user = getUserFromSession(session);
        if (docIds != null) {
            Set<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessProvider.canRemove(documents, user)) {
                userDocumentService.moveToTrash(docIds, userId);
                documents.forEach(doc ->
                        sendRemoveEvent(userDocumentService, "Document", doc.getName(), doc.getId(), user));
            }
        }
        if (dirIds != null) {
            Set<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessProvider.canRemove(directories, user)) {
                userDirectoryService.moveToTrash(dirIds, userId);
                directories.forEach(dir ->
                        sendRemoveEvent(userDirectoryService, "Directory", dir.getName(), dir.getId(), user));
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

    @RequestMapping(value = "/recover-document", method = RequestMethod.POST)
    public ModelAndView recoverDocument(long remDocId, HttpSession session) {
        User user = getUserFromSession(session);
        if (documentAccessProvider.canRecover(remDocId, user)) {
            Long docId = userDocumentService.recover(remDocId);

            String docName = userDocumentService.getById(docId).getName();
            sendRecoverEvent(userDocumentService, "Document", docName, docId, user);
            return new ModelAndView("redirect:/document/upload");
        }
        return null;
    }

    @RequestMapping(value = "/recover-directory", method = RequestMethod.POST)
    public ModelAndView recoverDirectory(long remDirId, HttpSession session) {
        User user = getUserFromSession(session);
        if (directoryAccessProvider.canRecover(remDirId, user)) {
            Long dirId = userDirectoryService.recover(remDirId);

            String dirName = userDirectoryService.getById(dirId).getName();
            sendRecoverEvent(userDirectoryService, "Directory", dirName, dirId, user);
            return new ModelAndView("redirect:/document/upload");
        }
        return null;
    }

    @RequestMapping(value = "/browse-{docId}", method = RequestMethod.GET)
    public ModelAndView browseDocument(@PathVariable long docId, HttpSession session) {
        ModelAndView model = new ModelAndView();
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessProvider.canRead(document, user)) {
            model.setViewName("document");
            model.addObject("doc", EntityToDtoConverter.convert(document));
            model.addObject("location", userDocumentService.getLocation(document));
            return model;
        }
        return null;
    }

    @RequestMapping(value = "/get-comments", method = RequestMethod.GET)
    public Set<CommentDto> getComments(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        if (documentAccessProvider.canRead(document, user)
                && document.getAbilityToComment() == AbilityToCommentDocument.ENABLE) {
            Set<CommentDto> comments = new TreeSet<>();
            document.getComments().forEach(c -> comments.add(EntityToDtoConverter.convert(c)));
            return comments;
        }
        return null;
    }

    @RequestMapping(value = "/add-comment", method = RequestMethod.POST)
    public CommentDto addComment(String text, long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessProvider.canRead(document, user)
                && document.getAbilityToComment() == AbilityToCommentDocument.ENABLE
                && !text.isEmpty()) {
            Comment comment = CommentUtil.createComment(text, user, document);
            commentService.save(comment);
            return EntityToDtoConverter.convert(comment);
        }
        return null;
    }

    @RequestMapping("/clear-comments")
    @ResponseStatus(HttpStatus.OK)
    public void clearComments(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        if (documentAccessProvider.isOwner(document, user)) {
            document.getComments().clear();
            userDocumentService.update(document);
        }
    }

    @RequestMapping(value = "/make-directory", method = RequestMethod.GET)
    public UserFileDto makeDir(String dirName,
                               @RequestParam(required = false, name = "dirHashName") String parentDirectoryHash,
                               HttpSession session) {

        User owner = getUserFromSession(session);
        if (parentDirectoryHash == null || parentDirectoryHash.isEmpty()) {
            parentDirectoryHash = owner.getLogin();
        }

        if (dirName != null && !dirName.isEmpty()) {
            UserDirectory directory = makeDirectory(owner, parentDirectoryHash, dirName);
            return EntityToDtoConverter.convert(directory);
        }
        return null;
    }

    @RequestMapping(value = "/get_document", method = RequestMethod.GET)
    public UserFileDto getUserDocument(long docId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(docId);
        if (documentAccessProvider.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            return EntityToDtoConverter.convert(document);
        }
        return null;
    }

    @RequestMapping("/get_directory")
    public UserFileDto getUserDirectory(Long dirId, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(dirId);
        if (directoryAccessProvider.isOwner(directory, user)
                && directory.getDocumentStatus() == DocumentStatus.ACTUAL) {
            return EntityToDtoConverter.convert(directory);
        }
        return null;
    }

    @RequestMapping(value = "/rename_document", method = RequestMethod.POST)
    public UserFileDto renameDocument(Long docId, String newDocName, HttpSession session) {
        UserDocument document = userDocumentService.getById(docId);
        User user = getUserFromSession(session);

        if (documentAccessProvider.isOwner(document, user)) {
            UserDocument documentWithNewName =
                    userDocumentService.getByFullNameAndOwner(user, document.getParentDirectoryHash(), newDocName);
            if (documentWithNewName == null && UserFileUtil.validateDocumentNameWithoutExtension(newDocName)) {
                String oldDocName = document.getName();
                document.setName(newDocName);
                userDocumentService.update(document);

                sendRenameEvent(userDocumentService.getAllReadersAndEditors(docId), "document", oldDocName,
                        newDocName, document.getId(), user);

                return EntityToDtoConverter.convert(document);
            }
        }
        return null;
    }

    @RequestMapping(value = "/rename_directory", method = RequestMethod.POST)
    public UserFileDto renameDirectory(Long dirId, String newDirName, HttpSession session) {
        UserDirectory directory = userDirectoryService.getById(dirId);
        User user = getUserFromSession(session);

        if (directoryAccessProvider.isOwner(directory, user)) {
            UserDirectory directoryWithNewName =
                    userDirectoryService.getByFullNameAndOwner(user, directory.getParentDirectoryHash(), newDirName);
            if (directoryWithNewName == null && UserFileUtil.validateDirectoryName(newDirName)) {
                String oldDirName = directory.getName();
                directory.setName(newDirName);
                userDirectoryService.update(directory);

                sendRenameEvent(userDirectoryService.getAllReaders(dirId), "directory", oldDirName,
                        newDirName, directory.getId(), user);

                return EntityToDtoConverter.convert(directory);
            }
        }
        return null;
    }

    @RequestMapping("/get_directories_names")
    public Map<String, String> getDirectoriesNames(HttpSession session) {
        User user = getUserFromSession(session);

        Set<UserDirectory> directories = userDirectoryService.getActualByOwner(user);
        Map<String, String> directoriesMap = directories.stream()
                .collect(Collectors
                        .toMap(d -> userDirectoryService.getLocation(d) + d.getName(), UserDirectory::getHashName));
        return directoriesMap;
    }

    @RequestMapping(value = "/share_document", method = RequestMethod.POST)
    public UserFileDto shareUserDocument(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getById(shared.getDocId());
        Set<User> readersAndEditors = userDocumentService.getAllReadersAndEditors(document.getId());

        if (documentAccessProvider.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            document.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));
            document.setReaders(createEntitySet(shared.getReaders(), userService));
            document.setEditors(createEntitySet(shared.getEditors(), userService));
            document.setReadersGroups(createEntitySet(shared.getReadersGroups(), friendsGroupService));
            document.setEditorsGroups(createEntitySet(shared.getEditorsGroups(), friendsGroupService));
            userDocumentService.update(document);

            Set<User> newReadersAndEditorsSet = userDocumentService.getAllReadersAndEditors(document.getId());
            newReadersAndEditorsSet.removeAll(readersAndEditors);
            sendShareEvent(newReadersAndEditorsSet, "document", document.getName(), document.getId(), user);

            newReadersAndEditorsSet = userDocumentService.getAllReadersAndEditors(document.getId());
            readersAndEditors.removeAll(newReadersAndEditorsSet);
            sendProhibitAccessEvent(readersAndEditors, "document", document.getName(), user);

            return EntityToDtoConverter.convert(document);
        }
        return null;
    }

    @RequestMapping(value = "/share_directory", method = RequestMethod.POST)
    public UserFileDto shareUserDirectory(@RequestBody SharedDto shared, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory directory = userDirectoryService.getById(shared.getDocId());
        Set<User> readers = userDirectoryService.getAllReaders(directory.getId());

        if (directoryAccessProvider.isOwner(directory, user)
                && directory.getDocumentStatus() == DocumentStatus.ACTUAL) {
            directory.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));
            directory.setReaders(createEntitySet(shared.getReaders(), userService));
            directory.setReadersGroups(createEntitySet(shared.getReadersGroups(), friendsGroupService));
            userDirectoryService.update(directory);

            Set<User> newReaderSet = userDocumentService.getAllReadersAndEditors(directory.getId());
            newReaderSet.removeAll(readers);
            sendShareEvent(newReaderSet, "directory", directory.getName(), directory.getId(), user);

            newReaderSet = userDocumentService.getAllReadersAndEditors(directory.getId());
            readers.removeAll(newReaderSet);
            sendProhibitAccessEvent(readers, "directory", directory.getName(), user);

            return EntityToDtoConverter.convert(directory);
        }
        return null;
    }

    @RequestMapping("/history-{docId}")
    public ModelAndView showHistory(@PathVariable Long docId, HttpSession session) {
        ModelAndView model = new ModelAndView("history");
        User user = getUserFromSession(session);
        UserDocument document = userDocumentService.getWithOldVersions(docId);
        if (documentAccessProvider.isOwner(document, user) && document.getDocumentStatus() == DocumentStatus.ACTUAL) {
            List<DocumentOldVersionDto> versions = new ArrayList<>();
            document.getDocumentOldVersions().forEach(v -> versions.add(EntityToDtoConverter.convert(v)));
            model.addObject("versions", versions);
            return model;
        }
        return null;
    }

    @RequestMapping("/accessible-documents")
    public ModelAndView getAccessibleDocuments(HttpSession session) {
        User user = getUserFromSession(session);
        Set<UserFileDto> documentDtos = new TreeSet<>();
        userDocumentService.getAllCanRead(user).forEach(d -> documentDtos.add(EntityToDtoConverter.convert(d)));
        ModelAndView model = new ModelAndView("accessibleDocuments");
        model.addObject("documents", documentDtos);
        return model;
    }

    @RequestMapping("/get-directory-content-{dirHashName}")
    public Set<UserFileDto> getDirectoryContent(@PathVariable String dirHashName, HttpSession session) {
        User user = getUserFromSession(session);
        if (dirHashName.equals("root")) {
            dirHashName = user.getLogin();
            return getDirectoryContent(dirHashName);
        } else {
            UserDirectory directory = userDirectoryService.getByHashName(dirHashName);
            if (directoryAccessProvider.canRead(directory, user)) {
                return getDirectoryContent(dirHashName);
            }
        }
        return null;
    }

    @RequestMapping("get-parent-directory-content-{dirHashName}")
    public Set<UserFileDto> getParentDirectoryContent(@PathVariable String dirHashName, HttpSession session) {
        User user = getUserFromSession(session);
        UserDirectory currentDirectory = userDirectoryService.getByHashName(dirHashName);
        if (directoryAccessProvider.canRead(currentDirectory, user)) {
            return getDirectoryContent(currentDirectory.getParentDirectoryHash());
        }
        return null;
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
    public void replaceFiles(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                             @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                             String destinationDirectoryHash,
                             HttpSession session) {

        User user = getUserFromSession(session);
        if (docIds != null && destinationDirectoryHash != null) {
            Set<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessProvider.isOwner(documents, user)) {
                userDocumentService.replace(docIds, destinationDirectoryHash);
            }
        }
        if (dirIds != null && destinationDirectoryHash != null) {
            Set<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessProvider.canRemove(directories, user)) {
                userDirectoryService.replace(dirIds, destinationDirectoryHash);
            }
        }
    }

    @RequestMapping(value = "/copy_files", method = RequestMethod.POST)
    public void copyFiles(@RequestParam(value = "docIds[]", required = false) Long[] docIds,
                          @RequestParam(value = "dirIds[]", required = false) Long[] dirIds,
                          String destinationDirectoryHash,
                          HttpSession session) {

        User user = getUserFromSession(session);
        if (docIds != null && destinationDirectoryHash != null) {
            Set<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessProvider.isOwner(documents, user)) {
                userDocumentService.replace(docIds, destinationDirectoryHash);
            }
        }
        if (dirIds != null && destinationDirectoryHash != null) {
            Set<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessProvider.canRemove(directories, user)) {
                userDirectoryService.replace(dirIds, destinationDirectoryHash);
            }
        }
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
                                      String parentDirectoryHash,
                                      String description,
                                      User user) throws IOException {

        String docName = multipartFile.getOriginalFilename();
        UserDocument document = userDocumentService.getByFullNameAndOwner(user, parentDirectoryHash, docName);

        if (document == null) {
            document = UserFileUtil.createUserDocument(multipartFile, parentDirectoryHash, description, user);
            multipartFile.transferTo(UserFileUtil.createFile(document.getHashName()));
            userDocumentService.save(document);
        } else if (document.getDocumentStatus() == DocumentStatus.REMOVED) {
            RemovedDocument removedDocument = removedDocumentService.getByUserDocument(document);
            Long docId = userDocumentService.recover(removedDocument.getId());
            document = userDocumentService.getDocumentWithOldVersions(docId);
            updateDocument(document, user, description, multipartFile);
        } else if (documentAccessProvider.canEdit(document, user)) {
            document = userDocumentService.getDocumentWithOldVersions(document.getId());
            updateDocument(document, user, description, multipartFile);
        }
    }

    private void updateDocument(UserDocument document, User user, String description, MultipartFile multipartFile)
            throws IOException {
        DocumentOldVersion oldVersion = DocumentVersionUtil.createOldVersion(document);
        document.getDocumentOldVersions().add(oldVersion);
        userDocumentService.update(UserFileUtil.updateUserDocument(document, multipartFile, description, user));
        sendUpdateEvent(document, user);
    }

    private UserDirectory makeDirectory(User owner, String parentDirectoryHash, String dirName) {
        UserDirectory directory = userDirectoryService.getByFullNameAndOwner(owner, parentDirectoryHash, dirName);

        if (directory == null && UserFileUtil.validateDirectoryName(dirName)) {
            directory = UserFileUtil.createUserDirectory(owner, parentDirectoryHash, dirName);
            long dirId = userDirectoryService.save(directory);
            directory.setId(dirId);
        }
        return directory;
    }

    private void sendUpdateEvent(UserDocument document, User user) {
        String eventText = "Document " + document.getName() + " has been updated by " + user.getFullName();
        String eventLinkText = "Browse";
        String eventLinkUrl = "/document/browse-" + document.getId();

        Set<User> readers = userDocumentService.getAllReadersAndEditors(document.getId());
        eventService.save(EventUtil.createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    private <T, S extends EntityService<T, Long>> void sendRemoveEvent(S service, String fileType, String fileName,
                                                                       long fileId, User user) {
        String eventText = fileType + " " + fileName + " has been removed by " + user.getFullName();

        Set<User> readers = service instanceof UserDirectoryService
                ? ((UserDirectoryService) service).getAllReaders(fileId)
                : ((UserDocumentService) service).getAllReadersAndEditors(fileId);
        eventService.save(EventUtil.createEvent(readers, eventText, user));
    }

    private <T, S extends EntityService<T, Long>> void sendRecoverEvent(S service, String fileType, String fileName,
                                                                        long fileId, User user) {
        String eventText = fileType + " " + fileName + " has been recovered by " + user.getFullName();
        String eventLinkText = null;
        String eventLinkUrl = null;
        if (fileType.toLowerCase().equals("Document")) {
            eventLinkText = "Browse";
            eventLinkUrl = "/document/browse-" + fileId;
        }

        Set<User> readers = service instanceof UserDirectoryService
                ? ((UserDirectoryService) service).getAllReaders(fileId)
                : ((UserDocumentService) service).getAllReadersAndEditors(fileId);

        eventService.save(EventUtil.createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    private void sendRenameEvent(Set<User> readers, String fileType, String fileOldName,
                                 String fileName, long fileId, User user) {

        String eventText = "User " + user.getFullName() + " has renamed "
                + fileType + " " + fileOldName + " to " + fileName;
        String eventLinkText = null;
        String eventLinkUrl = null;
        if (fileType.toLowerCase().equals("document")) {
            eventLinkText = "Browse";
            eventLinkUrl = "/document/browse-" + fileId;
        }
        eventService.save(EventUtil.createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    private void sendShareEvent(Set<User> readers, String fileType, String fileName, long fileId, User user) {
        String eventText = "User " + user.getFullName() + " has shared " + fileType + " " + fileName;
        String eventLinkText = null;
        String eventLinkUrl = null;
        if (fileType.toLowerCase().equals("document")) {
            eventLinkText = "Browse";
            eventLinkUrl = "/document/browse-" + fileId;
        }
        eventService.save(EventUtil.createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    private void sendProhibitAccessEvent(Set<User> readers, String fileType, String fileName, User user) {
        String eventText = "User " + user.getFullName() + " has prohibited access to " + fileType + " " + fileName;
        eventService.save(EventUtil.createEvent(readers, eventText, user));
    }
}
