package com.geekhub.controllers;

import com.geekhub.dto.RegistrationInfo;
import com.geekhub.dto.UserDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.exceptions.UserAuthenticationException;
import com.geekhub.exceptions.UserProfileException;
import com.geekhub.security.UserProfileManager;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.utils.UserFileUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/main")
public class MainController {

    @Inject
    private UserService userService;

    @Inject
    private UserProfileManager userProfileManager;

    @Inject
    private UserDocumentService userDocumentService;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("redirect:/api/documents");
    }

    @RequestMapping(value = "/sign_in", method = RequestMethod.GET)
    public ModelAndView signIn() {
        UserFileUtil.createRootDir();
        return new ModelAndView("signIn");
    }

    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public ModelAndView signIn(String j_username, String j_password, HttpSession session)
            throws UserAuthenticationException {

        ModelAndView model = new ModelAndView();
        User user;
        user = userProfileManager.authenticateUser(j_username, j_password);
        session.setAttribute("userId", user.getId());
        session.setAttribute("parentDirectoryHash", user.getLogin());
        session.setAttribute("currentLocation", user.getLogin());
        model.setViewName("redirect:/api/documents");
        return model;
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ModelAndView handleUserValidateException(UserAuthenticationException e) {
        return new ModelAndView("signIn", "errorMessage", e.getMessage());
    }

    @RequestMapping(value = "/sign_up", method = RequestMethod.GET)
    public String signUp() {
        return "signUp";
    }

    @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
    public ModelAndView signUp(RegistrationInfo registrationInfo) {

        ModelAndView model = new ModelAndView();
        try {
            userProfileManager.registerNewUser(registrationInfo);
            model.setViewName("signIn");
            model.addObject("message", "Your account created successfully");
        } catch (UserProfileException e) {
            model.addObject("registrationInfo", registrationInfo)
                    .addObject("errorMessage", e.getMessage())
                    .setViewName("signUp");
        }
        return model;
    }

    @RequestMapping("/sign_out")
    public String signOut(HttpSession session) {
        session.invalidate();
        return "signIn";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView search(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));

        List<User> users = userService.getAll("firstName");
        users.remove(user);
        Map<UserDto, Boolean> usersMap = users.stream()
                .collect(Collectors.toMap(EntityToDtoConverter::convert, u -> !userService.areFriends(user.getId(), u)));

        ModelAndView model = new ModelAndView("search");
        model.addObject("usersMap", usersMap);
        return model;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView search(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String country,
                               @RequestParam(required = false) String region,
                               @RequestParam(required = false) String city,
                               HttpSession session) {

        User user = userService.getById((Long) session.getAttribute("userId"));

        Map<String, String> searchingParametersMap = new HashMap<>();
        if (!country.isEmpty()) {
            searchingParametersMap.put("country", country);
        }
        if (!region.isEmpty()) {
            searchingParametersMap.put("state", region);
        }
        if (!city.isEmpty()) {
            searchingParametersMap.put("city", city);
        }

        ModelAndView model = new ModelAndView();
        Set<User> users;
        if (searchingParametersMap.size() > 0) {
            users = userService.search(name, searchingParametersMap);
            users.remove(user);
        } else if (!name.isEmpty()) {
            users = userService.searchByName(name);
            users.remove(user);
        } else {
            model.setViewName("redirect:/main/search");
            return model;
        }

        Map<UserDto, Boolean> usersMap = users.stream()
                .collect(Collectors.toMap(EntityToDtoConverter::convert, u -> !userService.areFriends(user.getId(), u)));

        model.setViewName("search");
        model.addObject("usersMap", usersMap);
        model.addObject("countOrResults", usersMap.size());
        model.addObject("name", name);
        searchingParametersMap.forEach(model::addObject);
        return model;
    }

    @RequestMapping("/userpage/{ownerId}")
    public ModelAndView userPage(@PathVariable Long ownerId, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        User owner = userService.getById(ownerId);

        Set<UserDocument> documents = new HashSet<>();
        documents.addAll(userDocumentService.getAllByOwnerAndAttribute(owner, DocumentAttribute.PUBLIC));
        if (userService.areFriends(ownerId, user)) {
            documents.addAll(userDocumentService.getAllByOwnerAndAttribute(owner, DocumentAttribute.FOR_FRIENDS));
        }
        Set<UserFileDto> fileDtoSet = new TreeSet<>();
        documents.forEach(d -> fileDtoSet.add(EntityToDtoConverter.convert(d)));

        ModelAndView model = new ModelAndView("userPage");
        model.addObject("pageOwner", EntityToDtoConverter.convert(owner));
        model.addObject("documents", fileDtoSet);
        return model;
    }
}
