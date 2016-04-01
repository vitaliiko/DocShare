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
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
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

    @InitBinder("fileBucket")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping("/view")
    public ModelAndView documentsList(HttpSession session) {
        ModelAndView model = new ModelAndView("documents");
        model.addObject("documents", userDocumentService.getByOwnerId((Long) session.getAttribute("userId")));
        return model;
    }

    @RequestMapping(value = "/download/{documentId}", method = RequestMethod.GET)
    public String downloadDocument(@PathVariable Long documentId, HttpServletResponse resp) throws IOException {
        UserDocument document = userDocumentService.getById(documentId);
        resp.setContentType(document.getType());
        resp.setContentLength(document.getContent().length);
        resp.setHeader("Content-Disposition","attachment; filename=\"" + document.getName() +"\"");
        FileCopyUtils.copy(document.getContent(), resp.getOutputStream());
        return "redirect:/view";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadDocument(@Valid FileBucket fileBucket, HttpSession session) throws IOException {
        User user = userService.getById((Long) session.getAttribute("userId"));
        UserDocument document = new UserDocument();

        MultipartFile multipartFile = fileBucket.getFile();

        document.setName(multipartFile.getOriginalFilename());
        document.setDescription(fileBucket.getDescription());
        document.setType(multipartFile.getContentType());
        document.setContent(multipartFile.getBytes());
        document.setOwner(user);
        userDocumentService.save(document);
        return "redirect:/view";
    }

//    @RequestMapping(value = "/delete", method = RequestMethod.GET)
//    public String deleteDocument(Long userId, Long docId) {
//        userDocumentService.deleteById(docId);
//        return "redirect:/view";
//    }

    @RequestMapping(value = { "/add-document-{userId}" }, method = RequestMethod.GET)
    public String addDocuments(@PathVariable Long userId, ModelMap model) {
        User user = userService.getById(userId);
        model.addAttribute("user", user);

        FileBucket fileModel = new FileBucket();
        model.addAttribute("fileBucket", fileModel);

        List<UserDocument> documents = userDocumentService.getByOwnerId(userId);
        model.addAttribute("documents", documents);

        return "managedocuments";
    }

    @RequestMapping(value = { "/download-document-{userId}-{docId}" }, method = RequestMethod.GET)
    public String downloadDocument(@PathVariable Long userId, @PathVariable Long docId, HttpServletResponse response) throws IOException {
        UserDocument document = userDocumentService.getById(docId);
        response.setContentType(document.getType());
        response.setContentLength(document.getContent().length);
        response.setHeader("Content-Disposition","attachment; filename=\"" + document.getName() +"\"");

        FileCopyUtils.copy(document.getContent(), response.getOutputStream());

        return "redirect:/add-document-"+userId;
    }

    @RequestMapping(value = { "/delete-document-{userId}-{docId}" }, method = RequestMethod.GET)
    public String deleteDocument(@PathVariable Long userId, @PathVariable Long docId) {
        userDocumentService.deleteById(docId);
        return "redirect:/add-document-"+userId;
    }

    @RequestMapping(value = { "/add-document-{userId}" }, method = RequestMethod.POST)
    public String uploadDocument(@Valid FileBucket fileBucket, BindingResult result, ModelMap model, @PathVariable Long userId) throws IOException{

        if (result.hasErrors()) {
            System.out.println("validation errors");
            User user = userService.getById(userId);
            model.addAttribute("user", user);

            List<UserDocument> documents = userDocumentService.getByOwnerId(userId);
            model.addAttribute("documents", documents);

            return "managedocuments";
        } else {

            System.out.println("Fetching file");

            User user = userService.getById(userId);
            model.addAttribute("user", user);

            saveDocument(fileBucket, user);

            return "redirect:/add-document-"+userId;
        }
    }

    private void saveDocument(FileBucket fileBucket, User user) throws IOException{

        UserDocument document = new UserDocument();

        MultipartFile multipartFile = fileBucket.getFile();

        document.setName(multipartFile.getOriginalFilename());
        document.setDescription(fileBucket.getDescription());
        document.setType(multipartFile.getContentType());
        document.setContent(multipartFile.getBytes());
        document.setOwner(user);
        userDocumentService.save(document);
    }
}
