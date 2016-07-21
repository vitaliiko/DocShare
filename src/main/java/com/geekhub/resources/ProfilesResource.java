package com.geekhub.resources;

import com.geekhub.dto.SearchDto;
import com.geekhub.dto.ExtendedUserDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.convertors.EntityToDtoConverter;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.exceptions.UserAuthenticationException;
import com.geekhub.exceptions.ValidateUserInformationException;
import com.geekhub.security.UserProfileManager;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProfilesResource {

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserProfileManager userProfileManager;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        model.addObject("user", EntityToDtoConverter.extendedConvert(user));
        return model;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView changeProfile(ExtendedUserDto userDto, HttpSession session) throws ValidateUserInformationException {
        ModelAndView model = new ModelAndView("profile");
        User user = userService.getById((Long) session.getAttribute("userId"));
        userProfileManager.updateUserProfile(userDto, user);

        model.addObject("message", "Your account updated successfully");
        model.addObject("user", userDto);
        return model;
    }

    @RequestMapping(value = "/profile/password", method = RequestMethod.POST)
    public ModelAndView changePassword(@RequestParam String currentPassword,
                                       @RequestParam String newPassword,
                                       HttpSession session) throws ValidateUserInformationException {

        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("profile");
        userProfileManager.changePassword(currentPassword, newPassword, user);

        model.addObject("message", "Your password updated successfully");
        model.addObject("user", EntityToDtoConverter.extendedConvert(user));
        return model;
    }

    @RequestMapping(value = "/profile/remove", method = RequestMethod.POST)
    public ModelAndView removeAccount(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        ModelAndView model = new ModelAndView("signIn");
        if (user != null) {
            userService.delete(user);
            session.invalidate();
            model.addObject("message", "Your account removed successfully");
        }
        return model;
    }

    @RequestMapping(value = "/search/page", method = RequestMethod.GET)
    public ModelAndView search(HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));

        List<User> users = userService.getAll("firstName");
        users.remove(user);
        Map<ExtendedUserDto, Boolean> usersMap = users.stream()
                .collect(Collectors.toMap(EntityToDtoConverter::extendedConvert, u -> !userService.areFriends(user.getId(), u)));

        ModelAndView model = new ModelAndView("search");
        model.addObject("usersMap", usersMap);
        return model;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView search(SearchDto searchDto, HttpSession session) {
        User user = userService.getById((Long) session.getAttribute("userId"));
        Set<User> users = userService.search(searchDto);

        ModelAndView model = new ModelAndView();

        Map<ExtendedUserDto, Boolean> usersMap = users.stream()
                .collect(Collectors.toMap(EntityToDtoConverter::extendedConvert, u -> !userService.areFriends(user.getId(), u)));
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
        model.addObject("pageOwner", EntityToDtoConverter.extendedConvert(owner));
        model.addObject("documents", fileDtoSet);
        return model;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAuthenticationException.class)
    public ModelAndView handleUserValidateException(UserAuthenticationException e) {
        return new ModelAndView("signIn", "errorMessage", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidateUserInformationException.class)
    public ModelAndView handlingUserProfileException(ValidateUserInformationException e) {
        ModelAndView model = new ModelAndView("profile");
        model.addObject("errorMessage", e.getMessage());
        if (e.getUserDto() != null) {
            model.addObject("user", e.getUserDto());
        }
        return model;
    }
}
