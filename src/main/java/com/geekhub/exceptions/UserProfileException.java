package com.geekhub.exceptions;

import com.geekhub.dto.RegistrationInfoDto;

public class UserProfileException extends Exception {

    private RegistrationInfoDto registrationInfoDto;

    public UserProfileException() {
    }

    public UserProfileException(String message) {
        super(message);
    }

    public UserProfileException(String message, RegistrationInfoDto registrationInfoDto) {
        super(message);
        this.registrationInfoDto = registrationInfoDto;
    }

    public UserProfileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserProfileException(Throwable cause) {
        super(cause);
    }

    public UserProfileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RegistrationInfoDto getRegistrationInfoDto() {
        return registrationInfoDto;
    }

    public void setRegistrationInfoDto(RegistrationInfoDto registrationInfoDto) {
        this.registrationInfoDto = registrationInfoDto;
    }
}
