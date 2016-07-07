package com.geekhub.security;

import com.geekhub.dto.RegistrationInfo;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.exceptions.UserAuthenticationException;
import com.geekhub.exceptions.UserProfileException;
import com.geekhub.services.UserDirectoryService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.dto.convertors.DtoToEntityConverter;
import com.geekhub.utils.UserFileUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import javax.inject.Inject;
import org.springframework.stereotype.Service;

@Service
public class UserProfileManager {

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[\\.a-zA-Z0-9_-]{6,20}$");
    private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z ']{2,25}$");
    private static final Pattern LAST_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z ']{2,25}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9\\._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,4}$");
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^[A-Z][a-zA-Z-]{2,30}$");

    @Inject
    private UserService userService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    public void registerNewUser(RegistrationInfo regInfo) throws UserProfileException {
        if (regInfo != null) {
            validateNames(regInfo.getFirstName(), regInfo.getLastName(), regInfo.getLogin());
            validatePassword(regInfo.getPassword(), regInfo.getConfirmationPassword());
            checkExistingUserWithSuchLogin(regInfo.getLogin());
            createNewUser(regInfo);
        }
    }

    private void checkExistingUserWithSuchLogin(String login) throws UserProfileException {
        if (userService.getByLogin(login) != null) {
            throw new UserProfileException("User with such login already exist");
        }
    }

    private void validatePassword(String password, String confirmationPassword) throws UserProfileException {
        if (password.length() < 8) {
            throw new UserProfileException("Password must contain at least 8 characters");
        }
        if (!password.equals(confirmationPassword)) {
            throw new UserProfileException("Passwords doesn't match");
        }
    }

    private void validateNames(String firstName, String lastName, String login) throws UserProfileException {
        if (firstName.length() < 2 || firstName.length() > 25) {
            throw new UserProfileException("First name must contain between 6 and 25 characters");
        }
        if (!FIRST_NAME_PATTERN.matcher(firstName).matches()) {
            throw new UserProfileException("Enter a valid first name");
        }
        if (lastName.length() < 2 || lastName.length() > 25) {
            throw new UserProfileException("Last name must contain between 6 and 25 characters");
        }
        if (!LAST_NAME_PATTERN.matcher(lastName).matches()) {
            throw new UserProfileException("Enter a valid last name");
        }
        if (login.length() < 6 || login.length() > 20) {
            throw new UserProfileException("Login must contain between 6 and 25 characters");
        }
        if (!LOGIN_PATTERN.matcher(login).matches()) {
            throw new UserProfileException("Enter a valid login");
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
                checkExistingUserWithSuchLogin(userDto.getLogin());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
                if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
                    throw new UserProfileException("Enter a valid email address");
                }
            }
            if (userDto.getCountry() != null && !userDto.getCountry().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCountry()).matches()) {
                    throw new UserProfileException("Country name contain forbidden characters");
                }
            }
            if (userDto.getState() != null && !userDto.getState().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getState()).matches()) {
                    throw new UserProfileException("State name contain forbidden characters");
                }
            }
            if (userDto.getCity() != null && !userDto.getCity().isEmpty()) {
                if (!LOCATION_PATTERN.matcher(userDto.getCity()).matches()) {
                    throw new UserProfileException("City name contain forbidden characters");
                }
            }
            changeFilesParentDirectoryHash(user.getLogin(), userDto.getLogin());
            user = DtoToEntityConverter.merge(userDto, user);
            userService.update(user);
        }
    }

    private void changeFilesParentDirectoryHash(String currentParentDirHash, String newParentDirHash) {
        List<UserDocument> documents = userDocumentService.getByParentDirectoryHash(currentParentDirHash);
        documents.forEach(d -> {
            d.setParentDirectoryHash(newParentDirHash);
            userDocumentService.update(d);
        });
        List<UserDirectory> directories = userDirectoryService.getByParentDirectoryHash(currentParentDirHash);
        directories.forEach(d -> {
            d.setParentDirectoryHash(newParentDirHash);
            userDirectoryService.update(d);
        });
    }

    public void changePassword(String currentPassword, String newPassword, String confirmationNewPassword, User user)
            throws UserProfileException {

        if (user != null) {
            validatePassword(newPassword, confirmationNewPassword);
            if (!user.getPassword().equals(DigestUtils.sha1Hex(currentPassword))) {
                throw new UserProfileException("Wrong current password");
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
