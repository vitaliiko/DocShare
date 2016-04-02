package com.geekhub.controller;

import com.geekhub.service.MessageService;
import com.geekhub.service.UserService;
import com.geekhub.util.MessageUtil;
import com.geekhub.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/", "/index"})
public class IndexController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtil userUtil;

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("redirect:/documents/upload");
    }

//    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.POST)
//    public String index(@RequestParam String messageText, HttpSession session) {
//        Message message = new Message();
//        message.setText(messageUtil.detectLink(messageText));
//        message.setDate(Calendar.getInstance().getTime());
//        userService.addMessage((Long) session.getAttribute("userId"), message);
//        return "redirect:/index";
//    }
}
