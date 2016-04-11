package com.geekhub.security;

import com.geekhub.dto.RegistrationInfo;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.User;
import com.geekhub.exceptions.UserValidateException;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.DtoToEntityConverter;
import com.geekhub.utils.UserFileUtil;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileManager {

    public static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");
    public static final Pattern FIRST_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z ']{2,20}$");
    public static final Pattern LAST_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z ']{2,20}$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9\\._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,4}$");
    public static final Pattern LOCATION_PATTERN = Pattern.compile("^[A-Z][a-zA-Z-]{2,30}$");

    @Autowired
    private UserService userService;

    public void registerNewUser(RegistrationInfo regInfo) throws UserValidateException {
        if (regInfo != null) {
            validateNames(regInfo.getFirstName(), regInfo.getLastName(), regInfo.getLogin());
            checkExistingUserWithLogin(regInfo.getLogin());
            checkConfirmationPassword(regInfo.getPassword(), regInfo.getConfirmationPassword());
            createNewUser(regInfo);
        }
    }

    private void checkExistingUserWithLogin(String login) throws UserValidateException {
        if (userService.getByLogin(login) != null) {
            throw new UserValidateException("User with such login already exist");
        }
    }

    private void checkConfirmationPassword(String password, String confirmationPassword) throws UserValidateException {
        if (!password.equals(confirmationPassword)) {
            throw new UserValidateException("Passwords doesn't match");
        }
    }

    private void validateNames(String firstName, String lastName, String login) throws UserValidateException {
        if (!FIRST_NAME_PATTERN.matcher(firstName).matches()) {
            throw new UserValidateException("First name contain forbidden characters");
        }
        if (!LAST_NAME_PATTERN.matcher(lastName).matches()) {
            throw new UserValidateException("Last name contain forbidden characters");
        }
        if (!LOGIN_PATTERN.matcher(login).matches()) {
            throw new UserValidateException("Login contain forbidden characters");
        }
    }

    private void createNewUser(RegistrationInfo regInfo) {
        User user = new User();
        user.setFirstName(regInfo.getFirstName());
        user.setLastName(regInfo.getFirstName());
        user.setLogin(regInfo.getLogin());
        user.setPassword(DigestUtils.sha1Hex(regInfo.getPassword()));
        userService.save(user);
    }

    public User authenticateUser(String login, String password) throws UserValidateException {
        User user = userService.getByLogin(login);
        if (user == null || !user.getPassword().equals(DigestUtils.sha1Hex(password))) {
            throw new UserValidateException("Wrong login or password");
        }
        return user;
    }

    public void updateUserProfile(UserDto userDto, User user) throws UserValidateException {
        if (userDto != null && user != null) {
            validateNames(userDto.getFirstName(), userDto.getLastName(), userDto.getLogin());
            if (!user.getLogin().equals(userDto.getLogin())) {
                checkExistingUserWithLogin(userDto.getLogin());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
                if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
                    throw new UserValidateException("E-Mail contain forbidden characters");
                }
            }
            if (userDto.getCountry() != null && !userDto.getCountry().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCountry()).matches()) {
                    throw new UserValidateException("Country contain forbidden characters");
                }
            }
            if (userDto.getState() != null && !userDto.getState().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getState()).matches()) {
                    throw new UserValidateException("State contain forbidden characters");
                }
            }
            if (userDto.getCity() != null && !userDto.getCity().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCity()).matches()) {
                    throw new UserValidateException("City contain forbidden characters");
                }
            }
            user = DtoToEntityConverter.merge(userDto, user);
            userService.update(user);
        }
    }

    public void changePassword(String currentPassword, String newPassword, String confirmationNewPassword, User user)
            throws UserValidateException {

        if (user != null) {
            checkConfirmationPassword(newPassword, confirmationNewPassword);
            if (!user.getPassword().equals(DigestUtils.sha1Hex(currentPassword))) {
                throw new UserValidateException("Wrong current password password");
            }
            user.setPassword(DigestUtils.sha1Hex(newPassword));
            userService.update(user);
        }
    }

    public void removeAccount(User user) {
        if (user != null) {
            userService.removeFromFriends(user);
            UserFileUtil.removeUserFiles(user);
            userService.delete(user);
        }
    }
}
