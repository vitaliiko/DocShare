package com.geekhub.controller;

import com.geekhub.entity.Message;
import com.geekhub.entity.User;
import com.geekhub.service.MessageService;
import com.geekhub.service.UserService;
import com.geekhub.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Calendar;

@Controller
public class IndexController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView model = new ModelAndView("index");
        model.addObject("messages", messageService.getAll("date"));
        return model;
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.POST)
    public String index(@RequestParam String messageText, HttpSession session) {
        Message message = new Message();
        message.setText(messageUtil.detectLink(messageText));
        message.setDate(Calendar.getInstance().getTime());
        userService.addMessage((Long) session.getAttribute("userId"), message);
        return "redirect:/index";
    }
}
