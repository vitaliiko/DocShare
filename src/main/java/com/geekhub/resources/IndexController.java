package com.geekhub.resources;

import com.geekhub.utils.UserFileUtil;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping({"/", "/index", "/api"})
public class IndexController {

    @PostConstruct
    public void init() {
        UserFileUtil.createRootDir();
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("redirect:/api/documents");
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("redirect:/api/documents");
    }
}
