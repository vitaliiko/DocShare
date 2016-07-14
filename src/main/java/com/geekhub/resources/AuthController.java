package com.geekhub.resources;

import com.geekhub.dto.RegistrationInfoDto;
import com.geekhub.entities.User;
import com.geekhub.exceptions.UserAuthenticationException;
import com.geekhub.exceptions.ValidateUserInformationException;
import com.geekhub.security.UserProfileManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Inject
    private UserProfileManager userProfileManager;

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
    public ModelAndView signUp(RegistrationInfoDto registrationInfo) throws ValidateUserInformationException {
        userProfileManager.registerNewUser(registrationInfo);
        return new ModelAndView("signIn", "message", "Your account created successfully");
    }

    @RequestMapping(value = "/sign_out", method = RequestMethod.GET)
    public ModelAndView signOut(HttpSession session) {
        session.invalidate();
        return new ModelAndView("signIn");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidateUserInformationException.class)
    public ModelAndView handleUserProfileException(ValidateUserInformationException e) {
        ModelAndView model = new ModelAndView();
        model.addObject("errorMessage", e.getMessage())
                .setViewName("signUp");
        if (e.getRegistrationInfoDto() != null) {
            model.addObject("registrationInfo", e.getRegistrationInfoDto());
        }
        return model;
    }
}
