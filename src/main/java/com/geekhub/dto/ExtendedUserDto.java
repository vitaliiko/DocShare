package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

public class ExtendedUserDto extends UserDto implements Comparable<ExtendedUserDto> {

    @Getter @Setter
    private String email;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String state;

    @Getter @Setter
    private String city;

    @Override
    public int compareTo(ExtendedUserDto o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
