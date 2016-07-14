package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "id")
public class UserDto implements Comparable<UserDto> {

    @Getter @Setter
    private long id;

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private String login;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String state;

    @Getter @Setter
    private String city;

    @Override
    public int compareTo(UserDto o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
