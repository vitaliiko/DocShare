package com.geekhub.controllers;

import com.geekhub.dto.RegistrationInfoDto;
import com.geekhub.dto.SearchDto;
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
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MainController {

    @Inject
    private UserService userService;

    @Inject
    private UserProfileManager userProfileManager;

    @Inject
    private UserDocumentService userDocumentService;

    @PostConstruct
    public void init() {
        UserFileUtil.createRootDir();
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("redirect:/api/documents");
    }

    @RequestMapping(value = "/sign_in", method = RequestMethod.GET)
    public ModelAndView signIn() {
        return new ModelAndView("signIn");
    }

    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public ModelAndView signIn(@RequestParam String j_username,
                               @RequestParam String j_password,
                               HttpSession session) throws UserAuthenticationException {

        ModelAndView model = new ModelAndView();
        User user;
        user = userProfileManager.authenticateUser(j_username, j_password);
        session.setAttribute("userId", user.getId());
        session.setAttribute("parentDirectoryHash", user.getLogin());
        session.setAttribute("currentLocation", user.getLogin());
        model.setViewName("redirect:/api/documents");
        return model;
    }

    @RequestMapping(value = "/sign_up", method = RequestMethod.GET)
    public ModelAndView signUp() {
        return new ModelAndView("signUp");
    }

    @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
    public ModelAndView signUp(RegistrationInfoDto registrationInfo) throws UserProfileException {
        userProfileManager.registerNewUser(registrationInfo);
        return new ModelAndView("signIn", "message", "Your account created successfully");
    }

    @RequestMapping(value = "/sign_out", method = RequestMethod.GET)
    public ModelAndView signOut(HttpSession session) {
        session.invalidate();
        return new ModelAndView("signIn");
    }

    @RequestMapping(value = "/search/page", method = RequestMethod.GET)
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

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView search(SearchDto searchDto, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        Set<User> users = userService.search(searchDto);

        ModelAndView model = new ModelAndView();

        Map<UserDto, Boolean> usersMap = users.stream()
                .collect(Collectors.toMap(EntityToDtoConverter::convert, u -> !userService.areFriends(user.getId(), u)));
        model.setViewName("search");
        model.addObject("usersMap", usersMap);
        model.addObject("countOfResults", usersMap.size());
        model.addObject("name", searchDto.getName());
        searchDto.toMap().forEach(model::addObject);
        return model;
    }

    @RequestMapping(value = "/userpage/{ownerId}", method = RequestMethod.GET)
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAuthenticationException.class)
    public ModelAndView handleUserValidateException(UserAuthenticationException e) {
        return new ModelAndView("signIn", "errorMessage", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserProfileException.class)
    public ModelAndView handleUserProfileException(UserProfileException e) {
        ModelAndView model = new ModelAndView();
        model.addObject("registrationInfo", e.getRegistrationInfoDto())
                .addObject("errorMessage", e.getMessage())
                .setViewName("signUp");
        return model;
    }
}
