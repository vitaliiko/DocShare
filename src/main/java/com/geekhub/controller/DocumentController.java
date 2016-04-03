package com.geekhub.controller;

import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import com.geekhub.service.CommentService;
import com.geekhub.service.UserDocumentService;
import com.geekhub.service.UserService;
import com.geekhub.util.CommentUtil;
import com.geekhub.util.UserDocumentUtil;
import com.geekhub.validation.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private FileValidator fileValidator;

    @Autowired
    private CommentService commentService;

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView addDocuments(HttpSession session) {
        ModelAndView model = new ModelAndView("home");
        List<UserDocument> allDocuments = userDocumentService.getActualByOwnerId((Long) session.getAttribute("userId"));
        model.addObject("documentsMap", UserDocumentUtil.prepareDocumentsListMap(allDocuments));
        return model;
    }

    @RequestMapping(value = "/download-{docId}", method = RequestMethod.GET)
    public String downloadDocument(@PathVariable Long docId, HttpSession session, HttpServletResponse response)
            throws IOException {
        UserDocument document = userDocumentService.getById(docId);
        response.setContentType(document.getType());
        response.setContentLength(document.getContent().length);
        response.setHeader("Content-Disposition","attachment; filename=\"" + document.getName() +"\"");

        FileCopyUtils.copy(document.getContent(), response.getOutputStream());

        return "redirect:/document/upload";
    }

    @RequestMapping(value = "/move-to-trash", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void moveDocumentToTrash(@RequestParam("docIds[]") Long[] docIds, HttpSession session) {
        userDocumentService.moveToTrash(docIds);
    }

    @RequestMapping("/recover")
    public ModelAndView recoverDocument(HttpSession session) {
        ModelAndView model = new ModelAndView("recover");
        List<UserDocument> documents = userDocumentService.getRemovedByOwnerId((Long) session.getAttribute("userId"));
        model.addObject("documents", documents);
        return model;
    }

    @RequestMapping("/recover-{docId}")
    public ModelAndView recoverDocument(@PathVariable Long docId, HttpSession session) {
        userDocumentService.recover(docId);
        return new ModelAndView("redirect:/document/upload");
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public List<UserDocument> uploadDocument(MultipartHttpServletRequest request, String description, HttpSession session)
            throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getById(userId);

        List<UserDocument> documents = new ArrayList<>();
        Map<String, MultipartFile> fileMap = request.getFileMap();
        for (String s : fileMap.keySet()) {
            MultipartFile mpf = request.getFile(s);
            UserDocument doc = saveOrUpdateDocument(mpf, description, user);
            if (doc != null) {
                documents.add(doc);
            }
        }
        return documents;
    }

    @RequestMapping("/browse-{docId}")
    public ModelAndView browseDocument(@PathVariable Long docId, HttpSession session) {
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        ModelAndView model = new ModelAndView("document");
        model.addObject("doc", document);
        return model;
    }

    @RequestMapping("/add-comment")
    @ResponseStatus(HttpStatus.OK)
    public void addComment(String text, Long docId, HttpSession session) {
        User owner = userService.getById((Long) session.getAttribute("userId"));
        UserDocument document = userDocumentService.getDocumentWithComments(docId);
        document.getComments().add(CommentUtil.createComment(text, owner));
        userDocumentService.update(document);
    }

    @RequestMapping("/delete-comment")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(Long commentId, HttpSession session) {
        commentService.deleteById(commentId);
    }

    private UserDocument saveOrUpdateDocument(MultipartFile multipartFile, String description, User user)
            throws IOException {
        UserDocument document = userDocumentService.getByNameAndOwnerId(user.getId(), multipartFile.getOriginalFilename());
        if (document == null) {
            document = UserDocumentUtil.createUserDocument(multipartFile, description, user);
            userDocumentService.save(document);
            return document;
        }
        userDocumentService.update(UserDocumentUtil.updateUserDocument(document, multipartFile, description));
        return null;
    }
}
