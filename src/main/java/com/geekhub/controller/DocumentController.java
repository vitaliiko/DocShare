package com.geekhub.controller;

import com.geekhub.entity.Comment;
import com.geekhub.entity.DocumentOldVersion;
import com.geekhub.entity.RemovedDirectory;
import com.geekhub.entity.RemovedDocument;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.entity.UserDocument;
import com.geekhub.entity.enums.DocumentAttribute;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.DocumentOldVersionDto;
import com.geekhub.dto.SharedDto;
import com.geekhub.security.UserDirectoryAccessProvider;
import com.geekhub.security.UserDocumentAccessProvider;
import com.geekhub.service.CommentService;
import com.geekhub.service.DocumentOldVersionService;
import com.geekhub.service.EntityService;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.RemovedDirectoryService;
import com.geekhub.service.RemovedDocumentService;
import com.geekhub.service.UserDirectoryService;
import com.geekhub.service.UserDocumentService;
import com.geekhub.service.UserService;
import com.geekhub.util.CommentUtil;
import com.geekhub.util.DocumentVersionUtil;
import com.geekhub.util.EntityToDtoConverter;
import com.geekhub.util.UserFileUtil;
import com.geekhub.validation.FileValidator;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
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
    private UserDirectoryAccessProvider directoryAccessProvider;

    @Autowired
    private UserDocumentAccessProvider documentAccessProvider;

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView addDocuments(HttpSession session) {
        ModelAndView model = new ModelAndView("home");
        Long userId = (Long) session.getAttribute("userId");
        model.addObject("tableNames", new String[] {"ALL", "PRIVATE", "PUBLIC", "FOR_FRIENDS"});
        model.addObject("friendsGroups", userService.getAllFriendsGroups(userId));
        model.addObject("friends", userService.getAllFriends(userId));
        return model;
    }

    @RequestMapping(value = "/download-{docId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void downloadDocument(@PathVariable Long docId, HttpSession session, HttpServletResponse response)
            throws IOException {

        User user = userService.getById((Long) session.getAttribute("userId"));
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
        User user = userService.getById(userId);
        if (docIds != null) {
            List<UserDocument> documents = userDocumentService.getByIds(Arrays.asList(docIds));
            if (documentAccessProvider.canRemove(documents, user)) {
                userDocumentService.moveToTrash(docIds, userId);
            }
        }
        if (dirIds != null) {
            List<UserDirectory> directories = userDirectoryService.getByIds(Arrays.asList(dirIds));
            if (directoryAccessProvider.canRemove(directories, user)) {
                userDirectoryService.moveToTrash(dirIds, userId);
            }
        }
    }

    @RequestMapping("/recover")
    public ModelAndView recoverDocument(HttpSession session) {
        ModelAndView model = new ModelAndView("recover");
        List<RemovedDocument> documents =
                removedDocumentService.getAllByOwnerId((Long) session.getAttribute("userId"));
        List<RemovedDirectory> directories =
                removedDirectoryService.getAllByOwnerId((Long) session.getAttribute("userId"));
        model.addObject("documents", documents);
        model.addObject("directories", directories);
        return model;
    }

    @RequestMapping("/recover-doc-{remDocId}")
    public ModelAndView recoverDocument(@PathVariable Long remDocId, HttpSession session) {
        userDocumentService.recover(remDocId);
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping("/recover-dir-{remDirId}")
    public ModelAndView recoverDirectory(@PathVariable Long remDirId, HttpSession session) {
        userDirectoryService.recover(remDirId);
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView uploadDocument(@RequestParam("files[]") MultipartFile[] files,
                                       String description,
                                       HttpSession session) throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        String parentDirectoryHash = (String) session.getAttribute("parentDirectoryHash");
        User user = userService.getById(userId);
        if (files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    saveOrUpdateDocument(file, parentDirectoryHash, description, user);
                }
            }
        }
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping("/browse-{docId}")
    public ModelAndView browseDocument(@PathVariable Long docId, HttpSession session) {
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        ModelAndView model = new ModelAndView("document");
        model.addObject("doc", document);
        return model;
    }

    @RequestMapping("/add-comment")
    public Comment addComment(String text, Long docId, HttpSession session) {
        User owner = userService.getById((Long) session.getAttribute("userId"));
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        if (text != null && !text.isEmpty() && docId != null) {
            Comment comment = CommentUtil.createComment(text, owner);
            document.getComments().add(comment);
            userDocumentService.update(document);
            return comment;
        }
        return null;
    }

    @RequestMapping("/delete-comment")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(Long commentId, HttpSession session) {
        commentService.deleteById(commentId);
    }

    @RequestMapping("/make-directory")
    public UserFileDto makeDir(String dirName, HttpSession session) {
        User owner = userService.getById((Long) session.getAttribute("userId"));
        String parentDirectoryHash = (String) session.getAttribute("parentDirectoryHash");
        UserDirectory directory = makeDirectory(owner, parentDirectoryHash, dirName);
        return EntityToDtoConverter.convert(directory);
    }

    @RequestMapping("/get_document")
    public UserFileDto getUserDocument(Long docId, HttpSession session) {
        UserDocument document = userDocumentService.getDocumentWithReadersAndEditors(docId);
        return EntityToDtoConverter.convert(document);
    }

    @RequestMapping("/get_directory")
    public UserFileDto getUserDirectory(Long dirId, HttpSession session) {
        UserDirectory directory = userDirectoryService.getDirectoryWithReaders(dirId);
        return EntityToDtoConverter.convert(directory);
    }

    @RequestMapping(value = "/share_document", method = RequestMethod.POST)
    public UserFileDto shareUserDocument(@RequestBody SharedDto shared, HttpSession session) {
        UserDocument document = userDocumentService.getDocumentWithReadersAndEditors(shared.getDocId());
        document.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));

        document.setReaders(createEntitySet(shared.getReaders(), userService));
        document.setReadersGroups(createEntitySet(shared.getReadersGroups(), friendsGroupService));
        document.setEditors(createEntitySet(shared.getEditors(), userService));
        document.setEditorsGroups(createEntitySet(shared.getEditorsGroups(), friendsGroupService));

        userDocumentService.update(document);
        return EntityToDtoConverter.convert(document);
    }

    @RequestMapping(value = "/share_directory", method = RequestMethod.POST)
    public UserFileDto shareUserDirectory(@RequestBody SharedDto shared, HttpSession session) {
        UserDirectory directory = userDirectoryService.getDirectoryWithReaders(shared.getDocId());
        directory.setDocumentAttribute(DocumentAttribute.valueOf(shared.getAccess()));

        directory.setReaders(createEntitySet(shared.getReaders(), userService));
        directory.setReadersGroups(createEntitySet(shared.getReadersGroups(), friendsGroupService));

        userDirectoryService.update(directory);
        return EntityToDtoConverter.convert(directory);
    }

    private <T, S extends EntityService<T, Long>> Set<T> createEntitySet(long[] ids, S service) {
        Set<T> entitySet = new HashSet<>();
        Arrays.stream(ids).forEach(id -> entitySet.add(service.getById(id)));
        return entitySet;
    }

    @RequestMapping("/history-{docId}")
    public ModelAndView showHistory(@PathVariable Long docId, HttpSession session) {
        UserDocument document = userDocumentService.getWithOldVersions(docId);
        ModelAndView model = new ModelAndView("history");
        List<DocumentOldVersionDto> versions = new ArrayList<>();
        document.getDocumentOldVersions().forEach(v -> versions.add(EntityToDtoConverter.convert(v)));
        model.addObject("versions", versions);
        return model;
    }

    @RequestMapping("/get-directory-content-{dirHashName}")
    public Set<UserFileDto> getDirectoryContent(@PathVariable String dirHashName, HttpSession session) {
        return getDirectoryContent(dirHashName);
    }

    @RequestMapping("get-parent-directory-content-{dirHashName}")
    public Set<UserFileDto> getParentDirectoryContent(@PathVariable String dirHashName, HttpSession session) {
        UserDirectory currentDirectory = userDirectoryService.getByHashName(dirHashName);
        return getDirectoryContent(currentDirectory.getParentDirectoryHash());
    }

    private Set<UserFileDto> getDirectoryContent(String directoryHashName) {
        List<UserDocument> documents;
        List<UserDirectory> directories;
        documents = userDocumentService.getAllByParentDirectoryHash(directoryHashName);
        directories = userDirectoryService.getAllByParentDirectoryHash(directoryHashName);

        Set<UserFileDto> dtoList = new TreeSet<>();
        if (documents != null) {
            documents.forEach(d -> {
                if (d.getOwner() != null) {
                    dtoList.add(EntityToDtoConverter.convert(d));
                }
            });
        }
        if (directories != null) {
            directories.forEach(d -> {
                if (d.getOwner() != null) {
                    dtoList.add(EntityToDtoConverter.convert(d));
                }
            });
        }
        return dtoList;
    }

    private void saveOrUpdateDocument(MultipartFile multipartFile,
                                      String parentDirectoryHash,
                                      String description,
                                      User user) throws IOException {

        String docName = multipartFile.getOriginalFilename();
        UserDocument document = userDocumentService.getByFullNameAndOwner(user, parentDirectoryHash, docName);

        if (document == null) {
            RemovedDocument removedDocument =
                    removedDocumentService.getByFullNameAndOwner(user, parentDirectoryHash, docName);
            if (removedDocument == null) {
                document = UserFileUtil.createUserDocument(multipartFile, parentDirectoryHash, description, user);
                multipartFile.transferTo(UserFileUtil.createFile(document.getHashName()));
                userDocumentService.save(document);
            } else {
                Long docId = userDocumentService.recover(removedDocument.getId());
                document = userDocumentService.getDocumentWithOldVersions(docId);
                updateDocument(document, user, description, multipartFile);
            }
        } else {
            document = userDocumentService.getDocumentWithOldVersions(document.getId());
            updateDocument(document, user, description, multipartFile);
        }
    }

    private void updateDocument(UserDocument document, User owner, String description, MultipartFile multipartFile)
            throws IOException {
        DocumentOldVersion oldVersion = DocumentVersionUtil.saveOldVersion(document, "Changed by " + owner.toString());
        document.getDocumentOldVersions().add(oldVersion);
        userDocumentService.update(UserFileUtil.updateUserDocument(document, multipartFile, description));
    }

    private UserDirectory makeDirectory(User owner, String parentDirectoryHash, String dirName) {
        UserDirectory directory = userDirectoryService.getByFullNameAndOwnerId(owner, parentDirectoryHash, dirName);

        if (directory == null) {
            directory = UserFileUtil.createUserDirectory(owner, parentDirectoryHash, dirName);
            long dirId = userDirectoryService.save(directory);
            directory.setId(dirId);
        }
        return directory;
    }
}
