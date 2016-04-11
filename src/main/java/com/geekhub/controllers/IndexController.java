package com.geekhub.controllers;

import com.geekhub.services.UserService;
import com.geekhub.providers.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/", "/index"})
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProvider userProvider;

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("redirect:/document/upload");
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
