package com.geekhub.security;

import com.geekhub.dto.RegistrationInfoDto;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.exceptions.UserAuthenticationException;
import com.geekhub.exceptions.ValidateUserInformationException;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.DtoToEntityConverter;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import javax.inject.Inject;
import org.springframework.stereotype.Service;

@Service
public class UserProfileManager {

    public static final int PASSWORD_MIN_SIZE = 8;
    public static final int PASSWORD_MAX_SIZE = 25;

    public static final int LOGIN_MIN_SIZE = 6;
    public static final int LOGIN_MAX_SIZE = 25;

    public static final int NAME_MIN_SIZE = 2;
    public static final int NAME_MAX_SIZE = 25;

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[\\.a-zA-Z0-9_-]{6,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z ']{2,25}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9\\._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,4}$");
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^[A-Z][a-zA-Z-]{2,30}$");

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    public void registerNewUser(RegistrationInfoDto regInfo) throws ValidateUserInformationException {
        try {
            if (regInfo != null) {
                validateNames(regInfo.getFirstName(), regInfo.getLastName(), regInfo.getLogin());
                validatePassword(regInfo.getPassword());
                checkExistingUserWithSuchLogin(regInfo.getLogin());
                createNewUser(regInfo);
            }
        } catch (ValidateUserInformationException e) {
            e.setRegistrationInfoDto(regInfo);
            throw e;
        }
    }

    private void checkExistingUserWithSuchLogin(String login) throws ValidateUserInformationException {
        if (userService.getByLogin(login) != null) {
            throw new ValidateUserInformationException("User with such login already exist");
        }
    }

    private void validatePassword(String password) throws ValidateUserInformationException {
        if (password.length() < PASSWORD_MIN_SIZE) {
            throw new ValidateUserInformationException("Password must contain at least " + PASSWORD_MIN_SIZE + " characters");
        }
        if (password.length() > PASSWORD_MAX_SIZE) {
            throw new ValidateUserInformationException("Password must contain not more than " + PASSWORD_MAX_SIZE + " characters");
        }
    }

    private void validateNames(String firstName, String lastName, String login) throws ValidateUserInformationException {
        if (firstName.length() < NAME_MIN_SIZE || firstName.length() > NAME_MAX_SIZE) {
            String message = "First name must contain between " + NAME_MIN_SIZE + " and " + NAME_MAX_SIZE + " characters";
            throw new ValidateUserInformationException(message);
        }
        if (!NAME_PATTERN.matcher(firstName).matches()) {
            throw new ValidateUserInformationException("Enter a valid first name");
        }
        if (lastName.length() < NAME_MIN_SIZE || lastName.length() > NAME_MAX_SIZE) {
            String message = "Last name must contain between " + NAME_MIN_SIZE + " and " + NAME_MAX_SIZE + " characters";
            throw new ValidateUserInformationException(message);
        }
        if (!NAME_PATTERN.matcher(lastName).matches()) {
            throw new ValidateUserInformationException("Enter a valid last name");
        }
        if (login.length() < LOGIN_MIN_SIZE || login.length() > LOGIN_MAX_SIZE) {
            String message = "Login must contain between " + NAME_MIN_SIZE + " and " + NAME_MAX_SIZE + " characters";
            throw new ValidateUserInformationException(message);
        }
        if (!LOGIN_PATTERN.matcher(login).matches()) {
            throw new ValidateUserInformationException("Enter a valid login");
        }
    }

    private void createNewUser(RegistrationInfoDto regInfo) {
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

    public void updateUserProfile(UserDto userDto, User user) throws ValidateUserInformationException {
        if (userDto != null && user != null) {
            validateNames(userDto.getFirstName(), userDto.getLastName(), userDto.getLogin());
            if (!user.getLogin().equals(userDto.getLogin())) {
                checkExistingUserWithSuchLogin(userDto.getLogin());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
                if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
                    throw new ValidateUserInformationException("Enter a valid email address", userDto);
                }
            }
            if (userDto.getCountry() != null && !userDto.getCountry().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCountry()).matches()) {
                    throw new ValidateUserInformationException("Country name contain forbidden characters", userDto);
                }
            }
            if (userDto.getState() != null && !userDto.getState().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getState()).matches()) {
                    throw new ValidateUserInformationException("State name contain forbidden characters", userDto);
                }
            }
            if (userDto.getCity() != null && !userDto.getCity().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCity()).matches()) {
                    throw new ValidateUserInformationException("City name contain forbidden characters", userDto);
                }
            }
            changeFilesParentDirectoryHash(user.getLogin(), userDto.getLogin());
            user = DtoToEntityConverter.merge(userDto, user);
            userService.update(user);
        }
    }

    private void changeFilesParentDirectoryHash(String currentParentDirHash, String newParentDirHash) {
        List<UserDocument> documents = userDocumentService.getAllByParentDirectoryHash(currentParentDirHash);
        documents.forEach(d -> {
            d.setParentDirectoryHash(newParentDirHash);
            userDocumentService.update(d);
        });
        List<UserDirectory> directories = userDirectoryService.getAllByParentDirectoryHash(currentParentDirHash);
        directories.forEach(d -> {
            d.setParentDirectoryHash(newParentDirHash);
            userDirectoryService.update(d);
        });
    }

    public void changePassword(String currentPassword, String newPassword, User user)
            throws ValidateUserInformationException {

        if (user != null) {
            validatePassword(newPassword);
            if (!user.getPassword().equals(DigestUtils.sha1Hex(currentPassword))) {
                throw new ValidateUserInformationException("Wrong current password");
            }
            user.setPassword(DigestUtils.sha1Hex(newPassword));
            userService.update(user);
        }
    }
}
