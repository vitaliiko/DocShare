package com.geekhub.exceptions;

import com.geekhub.dto.RegistrationInfoDto;
import com.geekhub.dto.UserDto;

public class ValidateUserInformationException extends Exception {

    private RegistrationInfoDto registrationInfoDto;

    private UserDto userDto;

    public ValidateUserInformationException(String message) {
        super(message);
    }

    public ValidateUserInformationException(String message, UserDto userDto) {
        super(message);
        this.userDto = userDto;
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
