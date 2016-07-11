package com.geekhub.exceptions;

import com.geekhub.dto.RegistrationInfoDto;
import com.geekhub.dto.UserDto;

public class ValidateUserInformationException extends Exception {

    private RegistrationInfoDto registrationInfoDto;

    private UserDto userDto;

    public ValidateUserInformationException() {}

    public ValidateUserInformationException(String message) {
        super(message);
    }

    public ValidateUserInformationException(String message, RegistrationInfoDto registrationInfoDto) {
        super(message);
        this.registrationInfoDto = registrationInfoDto;
    }

    public ValidateUserInformationException(String message, UserDto userDto) {
        super(message);
        this.userDto = userDto;
    }

    public ValidateUserInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateUserInformationException(Throwable cause) {
        super(cause);
    }

    public ValidateUserInformationException(String message, Throwable cause,
                                            boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RegistrationInfoDto getRegistrationInfoDto() {
        return registrationInfoDto;
    }

    public void setRegistrationInfoDto(RegistrationInfoDto registrationInfoDto) {
        this.registrationInfoDto = registrationInfoDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
