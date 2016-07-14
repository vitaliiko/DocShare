package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@EqualsAndHashCode
public class FriendGroupDto implements Comparable<FriendGroupDto> {

    @Getter @Setter
    private long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private Set<UserDto> friends;

    @Override
    public int compareTo(FriendGroupDto o) {
        return this.getName().compareTo(o.getName());
    }

}
