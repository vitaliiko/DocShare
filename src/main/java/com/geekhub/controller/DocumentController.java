package com.geekhub.controller;

import com.geekhub.entity.User;
import com.geekhub.entity.UserDocument;
import com.geekhub.service.UserDocumentService;
import com.geekhub.service.UserService;
import com.geekhub.validation.FileValidator;
import com.geekhub.wrapper.FileBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

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

    @InitBinder("fileBucket")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping("/download")
    public String downloadDocument(Long documentId, HttpServletResponse resp) throws IOException {
        UserDocument document = userDocumentService.getById(documentId);
        resp.setContentType(document.getType());
        resp.setContentLength(document.getContent().length);
        resp.setHeader("Content-Disposition","attachment; filename=\"" + document.getName() +"\"");
        FileCopyUtils.copy(document.getContent(), resp.getOutputStream());
        return "redirect:/main/home";
    }

    @RequestMapping("/upload")
    public String uploadDocument(@Valid FileBucket fileBucket, BindingResult result, Long userId) throws IOException {
        User user = userService.getById(userId);
        UserDocument document = new UserDocument();

        MultipartFile multipartFile = fileBucket.getFile();

        document.setName(multipartFile.getOriginalFilename());
        document.setDescription(fileBucket.getDescription());
        document.setType(multipartFile.getContentType());
        document.setContent(multipartFile.getBytes());
        document.setOwner(user);
        userDocumentService.save(document);
        return "redirect:/main/home";
    }
}
