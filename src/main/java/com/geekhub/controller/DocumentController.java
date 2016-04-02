package com.geekhub.controller;

import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import com.geekhub.enums.DocumentAttribute;
import com.geekhub.enums.DocumentStatus;
import com.geekhub.service.UserDocumentService;
import com.geekhub.service.UserService;
import com.geekhub.validation.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FileValidator fileValidator;

    @InitBinder("multipartFile")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView addDocuments(HttpSession session) {
        ModelAndView model = new ModelAndView("managedocuments");
        List<UserDocument> documents = userDocumentService.getActualByOwnerId((Long) session.getAttribute("userId"));
        model.addObject("documents", documents);
        return model;
    }

    @RequestMapping(value = "/download-{docId}", method = RequestMethod.GET)
    public String downloadDocument(@PathVariable Long docId, HttpSession session, HttpServletResponse response) throws IOException {
        UserDocument document = userDocumentService.getById(docId);
        response.setContentType(document.getType());
        response.setContentLength(document.getContent().length);
        response.setHeader("Content-Disposition","attachment; filename=\"" + document.getName() +"\"");

        FileCopyUtils.copy(document.getContent(), response.getOutputStream());

        return "redirect:/document/upload";
    }

    @RequestMapping(value = "/move-to-trash", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deleteDocument(Long docId, HttpSession session) {
        userDocumentService.moveToTrash(docId);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadDocument(MultipartFile file, String description, HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getById(userId);
        saveOrUpdateDocument(file, description, user);
        return "redirect:/document/upload";
    }

    private void saveOrUpdateDocument(MultipartFile multipartFile, String description, User user) throws IOException{
        UserDocument document = userDocumentService.getByNameAndOwnerId(user.getId(), multipartFile.getOriginalFilename());
        if (document == null) {
            document = new UserDocument();
            document.setName(multipartFile.getOriginalFilename());
            document.setDescription(description);
            document.setLastModifyTime(Calendar.getInstance().getTime());
            document.setType(multipartFile.getContentType());
            document.setContent(multipartFile.getBytes());
            document.setOwner(user);
            document.setDocumentAttribute(DocumentAttribute.PRIVATE);
            document.setDocumentStatus(DocumentStatus.ACTUAL);
            userDocumentService.save(document);
        } else {
            document.setDescription(description);
            document.setLastModifyTime(Calendar.getInstance().getTime());
            document.setContent(multipartFile.getBytes());
            userDocumentService.update(document);
        }
    }
}
