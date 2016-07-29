package com.geekhub.resources;

import com.geekhub.entities.User;
import com.geekhub.resources.utils.ModelUtil;
import com.geekhub.services.UserService;
import com.geekhub.utils.UserFileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({"/", "/index", "/api", "/docshare"})
public class IndexController {

    @Inject
    private UserService userService;

    @PostConstruct
    public void init() {
        UserFileUtil.createRootDir();
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("redirect:/api/home");
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView createUploadDocumentPageModel(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        return ModelUtil.prepareModelWithShareTable(user, userService, new ModelAndView("home"));
    }
}
