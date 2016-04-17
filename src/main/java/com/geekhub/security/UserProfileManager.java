package com.geekhub.security;

import com.geekhub.dto.RegistrationInfo;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.User;
import com.geekhub.exceptions.UserAuthenticationException;
import com.geekhub.exceptions.UserProfileException;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.DtoToEntityConverter;
import com.geekhub.utils.UserFileUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileManager {

    public static final Pattern LOGIN_PATTERN = Pattern.compile("^[\\.a-zA-Z0-9_-]{3,20}$");
    public static final Pattern FIRST_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z ']{2,20}$");
    public static final Pattern LAST_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z ']{2,20}$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9\\._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,4}$");
    public static final Pattern LOCATION_PATTERN = Pattern.compile("^[A-Z][a-zA-Z-]{2,30}$");

    @Autowired
    private UserService userService;

    @Autowired
    private UserDocumentService userDocumentService;

    public void registerNewUser(RegistrationInfo regInfo) throws UserProfileException {
        if (regInfo != null) {
            validateNames(regInfo.getFirstName(), regInfo.getLastName(), regInfo.getLogin());
            checkExistingUserWithLogin(regInfo.getLogin());
            checkConfirmationPassword(regInfo.getPassword(), regInfo.getConfirmationPassword());
            createNewUser(regInfo);
        }
    }

    private void checkExistingUserWithLogin(String login) throws UserProfileException {
        if (userService.getByLogin(login) != null) {
            throw new UserProfileException("User with such login already exist");
        }
    }

    private void checkConfirmationPassword(String password, String confirmationPassword) throws UserProfileException {
        if (!password.equals(confirmationPassword)) {
            throw new UserProfileException("Passwords doesn't match");
        }
    }

    private void validateNames(String firstName, String lastName, String login) throws UserProfileException {
        if (!FIRST_NAME_PATTERN.matcher(firstName).matches()) {
            throw new UserProfileException("First name contain forbidden characters");
        }
        if (!LAST_NAME_PATTERN.matcher(lastName).matches()) {
            throw new UserProfileException("Last name contain forbidden characters");
        }
        if (!LOGIN_PATTERN.matcher(login).matches()) {
            throw new UserProfileException("Login contain forbidden characters");
        }
    }

    private void createNewUser(RegistrationInfo regInfo) {
        User user = new User();
        user.setFirstName(regInfo.getFirstName());
        user.setLastName(regInfo.getLastName());
        user.setLogin(regInfo.getLogin());
        user.setPassword(DigestUtils.sha1Hex(regInfo.getPassword()));
        userService.save(user);
    }

    public User authenticateUser(String login, String password) throws UserAuthenticationException {
        User user = userService.getByLogin(login);
        if (user == null || !user.getPassword().equals(DigestUtils.sha1Hex(password))) {
            throw new UserAuthenticationException("Wrong login or password");
        }
        return user;
    }

    public void updateUserProfile(UserDto userDto, User user) throws UserProfileException {
        if (userDto != null && user != null) {
            validateNames(userDto.getFirstName(), userDto.getLastName(), userDto.getLogin());
            if (!user.getLogin().equals(userDto.getLogin())) {
                checkExistingUserWithLogin(userDto.getLogin());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
                if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
                    throw new UserProfileException("E-Mail contain forbidden characters");
                }
            }
            if (userDto.getCountry() != null && !userDto.getCountry().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCountry()).matches()) {
                    throw new UserProfileException("Country contain forbidden characters");
                }
            }
            if (userDto.getState() != null && !userDto.getState().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getState()).matches()) {
                    throw new UserProfileException("State contain forbidden characters");
                }
            }
            if (userDto.getCity() != null && !userDto.getCity().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCity()).matches()) {
                    throw new UserProfileException("City contain forbidden characters");
                }
            }
            user = DtoToEntityConverter.merge(userDto, user);
            userService.update(user);
        }
    }

    public void changePassword(String currentPassword, String newPassword, String confirmationNewPassword, User user)
            throws UserProfileException {

        if (user != null) {
            checkConfirmationPassword(newPassword, confirmationNewPassword);
            if (!user.getPassword().equals(DigestUtils.sha1Hex(currentPassword))) {
                throw new UserProfileException("Wrong current password password");
            }
            user.setPassword(DigestUtils.sha1Hex(newPassword));
            userService.update(user);
        }
    }

    public boolean removeAccount(User user) {
        if (user != null) {
            userService.removeFromFriends(user);
            List<String> filesHashNames = new ArrayList<>();
            userDocumentService.getAllByOwner(user).forEach(d -> filesHashNames.add(d.getHashName()));
            userService.delete(user);
                UserFileUtil.removeUserFiles(filesHashNames);
                return true;
        }
        return false;
    }
}
