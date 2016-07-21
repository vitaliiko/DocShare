package com.geekhub.exceptions;

import com.geekhub.dto.RegistrationInfoDto;
import com.geekhub.dto.ExtendedUserDto;

public class ValidateUserInformationException extends Exception {

    private RegistrationInfoDto registrationInfoDto;

    private ExtendedUserDto userDto;

    public ValidateUserInformationException(String message) {
        super(message);
    }

    public ValidateUserInformationException(String message, ExtendedUserDto userDto) {
        super(message);
        this.userDto = userDto;
    }

    public RegistrationInfoDto getRegistrationInfoDto() {
        return registrationInfoDto;
    }

    public void setRegistrationInfoDto(RegistrationInfoDto registrationInfoDto) {
        this.registrationInfoDto = registrationInfoDto;
    }

    public ExtendedUserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(ExtendedUserDto userDto) {
        this.userDto = userDto;
    }
}
