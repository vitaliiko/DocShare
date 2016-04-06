package com.geekhub.controller;

import com.geekhub.entity.Comment;
import com.geekhub.entity.DocumentOldVersion;
import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.RemovedDocument;
import com.geekhub.entity.User;
import com.geekhub.entity.UserDirectory;
import com.geekhub.entity.UserDocument;
import com.geekhub.json.DocumentJson;
import com.geekhub.json.SharedJson;
import com.geekhub.service.CommentService;
import com.geekhub.service.DocumentOldVersionService;
import com.geekhub.service.FriendsGroupService;
import com.geekhub.service.RemovedDocumentService;
import com.geekhub.service.UserDirectoryService;
import com.geekhub.service.UserDocumentService;
import com.geekhub.service.UserService;
import com.geekhub.util.CommentUtil;
import com.geekhub.util.DocumentVersionUtil;
import com.geekhub.util.UserFileUtil;
import com.geekhub.validation.FileValidator;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
    private FriendsGroupService friendsGroupService;

    @Autowired
    private DocumentOldVersionService documentOldVersionService;

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView addDocuments(HttpSession session) {
        ModelAndView model = new ModelAndView("home");
        Long userId = (Long) session.getAttribute("userId");
        List<UserDocument> allDocuments = userDocumentService.getAllByOwnerId(userId);
        List<UserDirectory> allDirectories = userDirectoryService.getAllByOwnerId(userId);
//        if (parentDirectoryHash == null && parentDirectoryHash.isEmpty()) {
//            parentDirectoryHash = "\\";
//        }
        session.setAttribute("location", UserFileUtil.ROOT_LOCATION);
        model.addObject("documentsMap", UserFileUtil.prepareUserFileListMap(allDocuments));
        model.addObject("directoriesMap", UserFileUtil.prepareUserFileListMap(allDirectories));
        model.addObject("friendsGroups", userService.getAllFriendsGroups(userId));
        model.addObject("friends", userService.getAllFriends(userId));
        return model;
    }

    @RequestMapping(value = "/download-{docId}", method = RequestMethod.GET)
    public String downloadDocument(@PathVariable Long docId, HttpSession session, HttpServletResponse response)
            throws IOException {

        User user = userService.getById((Long) session.getAttribute("userId"));
        UserDocument document = userDocumentService.getById(docId);
        File file = UserFileUtil.createFile(document.getHashName(), user.getRootDirectory());
        response.setContentType(document.getType());
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getName() +"\"");

        FileCopyUtils.copy(Files.newInputStream(file.toPath()), response.getOutputStream());

        return "redirect:/document/upload";
    }

    @RequestMapping(value = "/move-to-trash", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void moveDocumentToTrash(@RequestParam("docIds[]") Long[] docIds, HttpSession session) {
        userDocumentService.moveToTrash(docIds, (Long) session.getAttribute("userId"));
    }

    @RequestMapping("/recover")
    public ModelAndView recoverDocument(HttpSession session) {
        ModelAndView model = new ModelAndView("recover");
        List<RemovedDocument> documents = removedDocumentService.getAllByOwnerId((Long) session.getAttribute("userId"));
        model.addObject("documents", documents);
        return model;
    }

    @RequestMapping("/recover-{docId}")
    public ModelAndView recoverDocument(@PathVariable Long docId, HttpSession session) {
        userDocumentService.recover(docId);
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView uploadDocument(@RequestParam("files[]") MultipartFile[] files,
                                       String description,
                                       HttpSession session) throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        String parentDirectoryHash = (String) session.getAttribute("parentDirectoryHash");
        User user = userService.getById(userId);
        for (MultipartFile file : files) {
            saveOrUpdateDocument(file, parentDirectoryHash, description, user);
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
    public ModelAndView makeDir(String dirName, HttpSession session) {
        User owner = userService.getById((Long) session.getAttribute("userId"));
        String parentDirectoryHash = (String) session.getAttribute("parentDirectoryHash");
        makeDirectory(owner, parentDirectoryHash, dirName);
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping("/get_document")
    public DocumentJson getUserDocument(Long docId, HttpSession session) {
        UserDocument document = userDocumentService.getById(docId);
        Set<User> readers = userDocumentService.getReaders(docId);
        Set<FriendsGroup> readersGroups = userDocumentService.getReadersGroup(docId);
        return new DocumentJson(docId, document.getName(), readers, readersGroups);
    }

    @RequestMapping(value = "/share_document", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void shareUserDocument(@RequestBody SharedJson shared, HttpSession session) {
        UserDocument document = userDocumentService.getDocumentWithReaders(shared.getDocId());
        Set<User> readersSet = new HashSet<>();
        Arrays.stream(shared.getReaders()).forEach(id -> readersSet.add(userService.getById(id)));
        document.setReaders(readersSet);
        Set<FriendsGroup> readersGroupsSet = new HashSet<>();
        Arrays.stream(shared.getReadersGroups()).forEach(id -> readersGroupsSet.add(friendsGroupService.getById(id)));
        document.setReadersGroups(readersGroupsSet);
        userDocumentService.update(document);
    }

    private void saveOrUpdateDocument(MultipartFile multipartFile, String parentDirectoryHash, String description, User user)
            throws IOException {

        UserDocument document = userDocumentService.getByFullNameAndOwnerId(
                user, parentDirectoryHash, multipartFile.getOriginalFilename()
        );

        if (document == null) {
            document = UserFileUtil.createUserDocument(multipartFile, parentDirectoryHash, description, user);
            Long docId = userDocumentService.save(document);
            String docHashName = userDocumentService.getById(docId).getHashName();
            multipartFile.transferTo(UserFileUtil.createFile(docHashName, user.getRootDirectory()));
        } else {
            document = userDocumentService.getDocumentWithOldVersions(document.getId());
            DocumentOldVersion oldVersion = DocumentVersionUtil.saveOldVersion(document, "Changed by " + user.toString());
            userDocumentService.update(UserFileUtil.updateUserDocument(document, multipartFile, description));
            documentOldVersionService.save(oldVersion);
            multipartFile.transferTo(UserFileUtil.createFile(document.getHashName(), user.getRootDirectory()));
        }
    }

    private void makeDirectory(User owner, String parentDirectoryHash, String dirName) {
        UserDirectory directory = userDirectoryService.getByFullNameAndOwnerId(owner, parentDirectoryHash, dirName);

        if (directory == null) {
            directory = UserFileUtil.createUserDirectory(owner, parentDirectoryHash, dirName);
            userDirectoryService.save(directory);
        }
    }
}
