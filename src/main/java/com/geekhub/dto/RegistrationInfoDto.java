package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = {"firstName", "lastName", "login"})
public class RegistrationInfoDto {

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private String login;

    @Getter @Setter
    private String password;

}
